package commons.lib.extra.server.socket.secured;

import commons.lib.extra.security.asymetric.AsymmetricKeyHandler;
import commons.lib.extra.security.asymetric.PrivateKeyHandler;
import commons.lib.extra.security.asymetric.PublicKeyHandler;
import commons.lib.extra.server.socket.AsyncEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ContactRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ContactRegistry.class);

    private static final Map<String, SecretKeySpec> SYMMETRIC_KEYS = new HashMap<>();
    private static final Map<String, List<PrivateKey>> PRIVATE_KEYS = new HashMap<>();
    // TODO method that combines public key and trusted
    private static final Map<String, List<PublicKey>> PUBLIC_KEYS = new HashMap<>();
    public static final Set<String> TRUSTED = new HashSet<>();
    public static final Map<AsyncEvent, CompletableFuture<String>> AWAITED_EVENT = new HashMap<>();


    public static void storeSymmetricKey(String hostname, SecretKeySpec key) {
        logger.debug("Storing {} for {}.", "SymmetricKey", hostname);
        SYMMETRIC_KEYS.put(hostname, key);
    }

    public static void storePublicKeys(String hostname, List<PublicKey> keys) {
        logger.debug("Storing {} for {}.", "PublicKeys", hostname);
        PUBLIC_KEYS.put(hostname, keys);
    }

    public static void storePrivateKeys(String hostname, List<PrivateKey> keys) {
        logger.debug("Storing {} for {}.", "PrivateKeys", hostname);
        PRIVATE_KEYS.put(hostname, keys);
    }

    public static SecretKeySpec getSymmetricKey(String hostname) {
        logger.debug("Getting {} for {}.", "SymmetricKey", hostname);
        return SYMMETRIC_KEYS.get(hostname);
    }

    public static List<PublicKey> getPublicKeys(String hostname) {
        logger.debug("Getting {} for {}.", "PublicKeys", hostname);
        return PUBLIC_KEYS.get(hostname);
    }

    public static List<PrivateKey> getPrivateKeys(String hostname) {
        logger.debug("Getting {} for {}.", "PrivateKeys", hostname);
        return PRIVATE_KEYS.get(hostname);
    }

    public static List<PublicKey> getPublicKeysOrDefault(String hostname, List<PublicKey> generateKeyPairsAndGetPublicKeys) {
        logger.debug("Getting {} or default for {}.", "PublicKeys", hostname);
        return PUBLIC_KEYS.getOrDefault(hostname, generateKeyPairsAndGetPublicKeys);
    }

    private static void validateKeys(String host, List<PrivateKey> pvKeys, List<PublicKey> pubKeys) {
        if (pvKeys == null || pubKeys == null) {
            logger.debug("Missing keys for testing key pair for {} : {}, {}", host, pvKeys, pubKeys);
            return;
        }
        final PublicKeyHandler publicKeyHandler = new PublicKeyHandler();
        final PrivateKeyHandler privateKeyHandler = new PrivateKeyHandler();
        final String monText = "Message of test !";
        final byte[] bytes = monText.getBytes(StandardCharsets.UTF_8);
        try {
            final BufferedInputStream bufferedInputStream = publicKeyHandler.recursiveProcessor(new LinkedList<>(pubKeys), AsymmetricKeyHandler.toBufferedInputStream(bytes));
            final byte[] encrypted = bufferedInputStream.readAllBytes();
            final BufferedInputStream bufferedInputStream1 = privateKeyHandler.recursiveProcessor(new LinkedList<>(pvKeys), AsymmetricKeyHandler.toBufferedInputStream(encrypted));
            final byte[] deciphered = bufferedInputStream1.readAllBytes();
            final String decipheredString = new String(deciphered, StandardCharsets.UTF_8);
            if (decipheredString.equals(monText)) {
                logger.debug("Key pairs are working for {}", host);
            }
            assert decipheredString.equals(monText);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
    }
}
