package commons.lib.server.socket.secured.step1;

import commons.lib.security.asymetric.AsymmetricKeyHandler;
import commons.lib.security.symetric.SymmetricHandler;
import commons.lib.server.socket.MessageConsumer;
import commons.lib.server.socket.Wrapper;
import commons.lib.server.socket.message.ErrorMessage;
import commons.lib.server.socket.secured.ContactRegistry;
import commons.lib.server.socket.secured.step2.EncryptedPublicKeysMessage;
import commons.lib.server.socket.secured.step2.EncryptedPublicKeysMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GetServerPublicKeysMessageConsumer implements MessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(GetServerPublicKeysMessageConsumer.class);

    @Override
    public Optional<Wrapper> process(Wrapper input, String consumerHostname, int consumerPort) {
        final GetServerPublicKeysMessage serverPublicKeysMessage = (GetServerPublicKeysMessage) input.getDatum();
        final String symKey = serverPublicKeysMessage.getSymKey();
        final int nbrPublicKeyRequested = serverPublicKeysMessage.getNbrPublicKeyRequested();
        final List<PublicKey> publicKeys = new ArrayList<>();
        final List<PrivateKey> privateKeys = new ArrayList<>();
        try {
            for (int i = 0; i < nbrPublicKeyRequested; i++) {
                final KeyPair pair = AsymmetricKeyHandler.createPair();
                final PublicKey aPublic = pair.getPublic();
                final PrivateKey aPrivate = pair.getPrivate();
                privateKeys.add(aPrivate);
                publicKeys.add(aPublic);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();// TODO ERROr
            return Optional.of(new Wrapper(ErrorMessage.CODE, new ErrorMessage("", e.getMessage(), consumerHostname, consumerPort, false)));
        }

        final SecretKeySpec secretKey = SymmetricHandler.getKey(symKey, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
        final List<byte[]> encryptedPublicKeys = new ArrayList<>();
        final List<Integer> sizedEncryptedPublicKeys = new ArrayList<>();
        for (PublicKey publicKey : publicKeys) {
            final byte[] encoded = publicKey.getEncoded();
            try {
                final byte[] encrypt = SymmetricHandler.encrypt(secretKey, encoded, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
                encryptedPublicKeys.add(encrypt);
                logger.info("Size of the encrypted key : {}", encrypt.length);
                sizedEncryptedPublicKeys.add(encrypt.length);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();// TODO ERROr
                return Optional.of(new Wrapper(ErrorMessage.CODE, new ErrorMessage("", e.getMessage(), consumerHostname, consumerPort, false)));
            }
        }
        ContactRegistry.storeSymmetricKey(serverPublicKeysMessage.getResponseHostname(), secretKey);
        ContactRegistry.storePublicKeys(consumerHostname, publicKeys);
        ContactRegistry.TRUSTED.add(consumerHostname);
        ContactRegistry.storePrivateKeys(consumerHostname, privateKeys);
        final Wrapper responseWrapper = new Wrapper(EncryptedPublicKeysMessage.CODE, new EncryptedPublicKeysMessage(sizedEncryptedPublicKeys, encryptedPublicKeys, true, false, consumerHostname, consumerPort, true));
        return Optional.of(responseWrapper);
    }
}
