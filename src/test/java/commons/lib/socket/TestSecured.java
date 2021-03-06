package commons.lib.socket;

import commons.lib.extra.security.symetric.SymmetricHandler;
import commons.lib.extra.server.socket.Wrapper;
import commons.lib.extra.server.socket.secured.ContactRegistry;
import commons.lib.extra.server.socket.secured.step1.GetServerPublicKeysMessage;
import commons.lib.extra.server.socket.secured.step1.GetServerPublicKeysMessageConsumer;
import commons.lib.extra.server.socket.secured.step2.EncryptedPublicKeysMessageConsumer;
import commons.lib.extra.server.socket.secured.step4.AcknowledgePublicKeysMessage;
import org.junit.Test;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestSecured {

    @Test
    public void test() {
        try (InputStream input = TestSecured.class.getClassLoader().getResourceAsStream("test.properties")) {
            assertNotNull("Properties file not found.", input);
            Properties properties = new Properties();
            properties.load(input);
            System.out.println(properties.get("test.input"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String password = "unitTest";
        final SecretKeySpec symSecretKey = SymmetricHandler.getKey(password, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
        ContactRegistry.storeSymmetricKey("Caller", symSecretKey);
        ContactRegistry.storeSymmetricKey("Consumer", symSecretKey);
        // Caller ask for consumer's public keys
        final GetServerPublicKeysMessage getServerPublicKeysMessage = new GetServerPublicKeysMessage(password, 1, "Caller", 50, true);
        final GetServerPublicKeysMessageConsumer getServerPublicKeysMessageConsumer = new GetServerPublicKeysMessageConsumer();
        final Wrapper wrapper = new Wrapper(GetServerPublicKeysMessage.CODE, getServerPublicKeysMessage);
        // Consumer is processing the request and respond with his encrypted response (sym) with his public keys
        final Optional<Wrapper> responseFromGetPublicKeysMessage = getServerPublicKeysMessageConsumer.process(wrapper, "Consumer", 51);
        final Wrapper encryptedPublicKeysMessageWrapper = responseFromGetPublicKeysMessage.get();
        final EncryptedPublicKeysMessageConsumer encryptedPublicKeysMessageConsumer = new EncryptedPublicKeysMessageConsumer();
        // Caller is processing the response and sends his public keys encrypted with the public keys consumer just responded
        final Optional<Wrapper> responseFromGetPublicKeysMessage2 = encryptedPublicKeysMessageConsumer.process(encryptedPublicKeysMessageWrapper, "Caller", 50);
        final Wrapper encryptedPublicKeysMessageWrapper2 = responseFromGetPublicKeysMessage2.get();
        // Consumer stores the public keys of Caller and returns an acknowledgment.
        final Optional<Wrapper> acknowledgment = encryptedPublicKeysMessageConsumer.process(encryptedPublicKeysMessageWrapper2, "Consumer", 51);
        final Wrapper wrapper1 = acknowledgment.get();
        assertTrue("The expected message should be an acknowledgement.", wrapper1.getDatum() instanceof AcknowledgePublicKeysMessage);
    }

}
