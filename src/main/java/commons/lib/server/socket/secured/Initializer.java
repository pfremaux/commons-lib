package commons.lib.server.socket.secured;

import commons.lib.server.socket.Wrapper;
import commons.lib.server.socket.WrapperFactory;
import commons.lib.server.socket.secured.step1.GetServerPublicKeysMessage;
import commons.lib.server.socket.secured.step2.EncryptedPublicKeysMessage;
import commons.lib.server.socket.secured.step4.AcknowledgePublicKeysMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Initializer {

    public static WrapperFactory init() {
        final Map<Integer, Function<List<String>, Wrapper>> all = new HashMap<>();
        all.put(GetServerPublicKeysMessage.CODE, strings -> new Wrapper(GetServerPublicKeysMessage.CODE, new GetServerPublicKeysMessage(strings)));
        all.put(EncryptedPublicKeysMessage.CODE, strings -> new Wrapper(EncryptedPublicKeysMessage.CODE, new EncryptedPublicKeysMessage(strings)));
        all.put(AcknowledgePublicKeysMessage.CODE, strings -> new Wrapper(AcknowledgePublicKeysMessage.CODE, new AcknowledgePublicKeysMessage(strings)));
        return new WrapperFactory(all);
    }

    public static WrapperFactory init(WrapperFactory factory) {
        final Map<Integer, Function<List<String>, Wrapper>> existingBuilders = factory.getFunctionMap();
        final Map<Integer, Function<List<String>, Wrapper>> all = new HashMap<>(existingBuilders);
        all.put(GetServerPublicKeysMessage.CODE, strings -> new Wrapper(GetServerPublicKeysMessage.CODE, new GetServerPublicKeysMessage(strings)));
        all.put(EncryptedPublicKeysMessage.CODE, strings -> new Wrapper(EncryptedPublicKeysMessage.CODE, new EncryptedPublicKeysMessage(strings)));
        all.put(AcknowledgePublicKeysMessage.CODE, strings -> new Wrapper(AcknowledgePublicKeysMessage.CODE, new AcknowledgePublicKeysMessage(strings)));
        return new WrapperFactory(all);
    }

}
