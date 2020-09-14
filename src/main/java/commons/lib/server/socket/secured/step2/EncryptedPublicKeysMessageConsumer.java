package commons.lib.server.socket.secured.step2;

import commons.lib.security.asymetric.AsymmetricKeyHandler;
import commons.lib.security.asymetric.PrivateKeyHandler;
import commons.lib.security.asymetric.PublicKeyHandler;
import commons.lib.security.symetric.SymmetricHandler;
import commons.lib.server.socket.MessageConsumer;
import commons.lib.server.socket.Wrapper;
import commons.lib.server.socket.message.ErrorMessage;
import commons.lib.server.socket.secured.ContactRegistry;
import commons.lib.server.socket.secured.step4.AcknowledgePublicKeysMessage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class EncryptedPublicKeysMessageConsumer implements MessageConsumer {

    @Override
    public Optional<Wrapper> process(Wrapper input, String consumerHostname, int consumerPort) {
        final EncryptedPublicKeysMessage datum = (EncryptedPublicKeysMessage) input.getDatum();
        final List<byte[]> encryptedPublicKeys = datum.getEncryptedPublicKeys();
        final int nbrPublicKeys = encryptedPublicKeys.size();
        final String callerHostName = datum.getResponseHostname();
        final List<PublicKey> publicKeysOfCaller = new ArrayList<>();
        if (datum.isSymmetric()) {
            // The data we received are encrypted with a symmetric key. We're taking the symmetric linked to the caller
            final SecretKeySpec secretKeySpec = ContactRegistry.SYMMETRIC_KEYS.get(callerHostName);
            for (byte[] encryptedPublicKey : encryptedPublicKeys) {
                try {
                    final byte[] decryptedPublicKey = SymmetricHandler.decrypt(secretKeySpec, encryptedPublicKey, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
                    final PublicKey publicKey =
                            KeyFactory.getInstance(AsymmetricKeyHandler.ASYMMETRIC_ALGORITHM).generatePublic(new X509EncodedKeySpec(decryptedPublicKey));
                    publicKeysOfCaller.add(publicKey);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException e) {
                    e.printStackTrace(); // TODO log ERROR
                    return Optional.of(new Wrapper(ErrorMessage.CODE, new ErrorMessage("", e.getMessage(), consumerHostname, consumerPort, false)));
                }
            }
            ContactRegistry.PUBLIC_KEYS.put(callerHostName, publicKeysOfCaller);
            ContactRegistry.TRUSTED.add(callerHostName);
            // If it's symmetric, it means the caller doesn't know our public keys.
            // Lets send them to him
            List<PublicKey> consumerPublicKeys = ContactRegistry.PUBLIC_KEYS.getOrDefault(consumerHostname, generateKeyPairsAndGetPublicKeys(nbrPublicKeys, consumerHostname));
            List<byte[]> encryptedPublicKeysOfConsumer = new ArrayList<>();
            final PublicKeyHandler publicKeyHandler = new PublicKeyHandler();
            for (PublicKey consumerPublicKey : consumerPublicKeys) {
                try {
                    final BufferedInputStream bufferedInputStream = publicKeyHandler.recursiveProcessor(new LinkedList<>(publicKeysOfCaller), new BufferedInputStream(new ByteArrayInputStream(consumerPublicKey.getEncoded())));
                    encryptedPublicKeysOfConsumer.add(bufferedInputStream.readAllBytes());
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                    e.printStackTrace(); // TODO ERROR
                    return Optional.of(new Wrapper(ErrorMessage.CODE, new ErrorMessage("", e.getMessage(), consumerHostname, consumerPort, false)));
                }
            }

            Wrapper responseWrapper = new Wrapper(EncryptedPublicKeysMessage.CODE, new EncryptedPublicKeysMessage(encryptedPublicKeysOfConsumer, false, true, consumerHostname, consumerPort, true));
            return Optional.of(responseWrapper);
        } else {
            if (!datum.isKnowRecipientData()) {
                // The caller doesn't know the public keys of this server. We're getting his public keys and we're encrypting and sending OUR public keys.
                final List<PrivateKey> consumerPrivateKeys = ContactRegistry.PRIVATE_KEYS.get(consumerHostname);
                final List<byte[]> decipheredPublicKeysOfCaller = new ArrayList<>();
                final PrivateKeyHandler privateKeyHandler = new PrivateKeyHandler();
                for (byte[] encryptedPublicKey : encryptedPublicKeys) {
                    try {
                        final BufferedInputStream bufferedInputStream = privateKeyHandler.recursiveProcessor(new LinkedList<>(consumerPrivateKeys), new BufferedInputStream(new ByteArrayInputStream(encryptedPublicKey)));
                        decipheredPublicKeysOfCaller.add(bufferedInputStream.readAllBytes());
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                        e.printStackTrace(); // TODO ERROR
                        return Optional.of(new Wrapper(ErrorMessage.CODE, new ErrorMessage("", e.getMessage(), consumerHostname, consumerPort, false)));
                    }
                }

                final KeyFactory kf;
                try {
                    kf = KeyFactory.getInstance("RSA");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return Optional.of(new Wrapper(ErrorMessage.CODE, new ErrorMessage("", e.getMessage(), consumerHostname, consumerPort, false)));
                }
                final List<PublicKey> callerPublicKeys = new ArrayList<>();
                for (byte[] decipheredPublicKeyOfCaller : decipheredPublicKeysOfCaller) {
                    try {
                        final PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(decipheredPublicKeyOfCaller));
                        callerPublicKeys.add(publicKey);
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                }
                ContactRegistry.PUBLIC_KEYS.put(callerHostName, callerPublicKeys);
                ContactRegistry.TRUSTED.add(callerHostName);
            }
            return Optional.of(new Wrapper(AcknowledgePublicKeysMessage.CODE, new AcknowledgePublicKeysMessage(consumerHostname, consumerPort, false)));
        }
    }

    private List<PublicKey> generateKeyPairsAndGetPublicKeys(int nbrPublicKeys, String hostname) {
        final List<PublicKey> publicKeys = new ArrayList<>();
        final List<PrivateKey> privateKeys = new ArrayList<>();
        try {
            for (int i = 0; i < nbrPublicKeys; i++) {
                final KeyPair pair = AsymmetricKeyHandler.createPair();
                final PublicKey aPublic = pair.getPublic();
                final PrivateKey aPrivate = pair.getPrivate();
                privateKeys.add(aPrivate);
                publicKeys.add(aPublic);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();// TODO ERROr
        }

        ContactRegistry.PUBLIC_KEYS.put(hostname, publicKeys);
        ContactRegistry.TRUSTED.add(hostname);
        ContactRegistry.PRIVATE_KEYS.put(hostname, privateKeys);
        return publicKeys;
    }
}
