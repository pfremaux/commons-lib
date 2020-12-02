package commons.lib.extra.server.socket.secured;

import commons.lib.extra.server.socket.*;
import commons.lib.extra.server.socket.secured.step1.GetServerPublicKeysMessage;
import commons.lib.extra.server.socket.secured.step1.GetServerPublicKeysMessageConsumer;
import commons.lib.extra.server.socket.secured.step2.EncryptedPublicKeysMessage;
import commons.lib.extra.server.socket.secured.step2.EncryptedPublicKeysMessageConsumer;
import commons.lib.extra.server.socket.secured.step4.AcknowledgePublicKeysMessage;
import commons.lib.extra.server.socket.secured.step4.AcknowledgePublicKeysMessageConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecuredSocketInitializer {


    public static WrapperFactory init() {
        final Map<Integer, Function<List<byte[]>, Wrapper>> all = new HashMap<>();
        all.put(GetServerPublicKeysMessage.CODE, strings -> {
            return new Wrapper(GetServerPublicKeysMessage.CODE, new GetServerPublicKeysMessage(
                    Message.bytesToString(strings.get(4)), // TODO bytes
                    Message.bytesToInt(strings.get(5)),
                    Message.bytesToString(strings.get(1)),
                    Message.bytesToInt(strings.get(2)),
                    Message.bytesToBool(strings.get(3))));
        });
        all.put(EncryptedPublicKeysMessage.CODE, strings -> {
            return new Wrapper(EncryptedPublicKeysMessage.CODE, new EncryptedPublicKeysMessage(
                    Stream.of(Message.bytesToString(strings.get(6)).split("-")).map(Integer::parseInt).collect(Collectors.toList()),
                    strings.get(7),
                    Message.bytesToBool(strings.get(4)),
                    Message.bytesToBool(strings.get(5)),
                    Message.bytesToString(strings.get(1)),
                    Message.bytesToInt(strings.get(2)),
                    Message.bytesToBool(strings.get(3))
            ));
        });
        all.put(AcknowledgePublicKeysMessage.CODE, strings -> {
            return new Wrapper(AcknowledgePublicKeysMessage.CODE, new AcknowledgePublicKeysMessage(
                    Message.bytesToString(strings.get(1)),
                    Message.bytesToInt(strings.get(2)),
                    Message.bytesToBool(strings.get(3))
            ));
        });
        return new WrapperFactory(all);
    }

    public static WrapperFactory init(WrapperFactory factory) {
        final Map<Integer, Function<List<byte[]>, Wrapper>> existingBuilders = factory.getFunctionMap();
        final Map<Integer, Function<List<byte[]>, Wrapper>> all = new HashMap<>(existingBuilders);
        all.put(GetServerPublicKeysMessage.CODE, strings -> {
            return new Wrapper(GetServerPublicKeysMessage.CODE, new GetServerPublicKeysMessage(
                    Message.bytesToString(strings.get(4)), // TODO bytes
                    Message.bytesToInt(strings.get(5)),
                    Message.bytesToString(strings.get(1)),
                    Message.bytesToInt(strings.get(2)),
                    Message.bytesToBool(strings.get(3))));
        });
        all.put(EncryptedPublicKeysMessage.CODE, strings -> {
            int certificateChunksNumber = strings.size() - 7;
            byte[][] certificatesChunks = new byte[certificateChunksNumber][];
            int certificateTotalSize = 0;
            for (int i = 7; i < strings.size(); i++) {
                certificatesChunks[i - 7] = strings.get(i);
                certificateTotalSize += certificatesChunks[i - 7].length;
            }
            byte[] certificates = new byte[certificateTotalSize + certificateChunksNumber];
            int indexPosition = 0;
            for (byte[] certificateChunk : certificatesChunks) {
                System.arraycopy(certificateChunk, 0, certificates, indexPosition, certificateChunk.length);
                indexPosition += certificateChunk.length;
                if (indexPosition < certificates.length - 1) {
                    certificates[indexPosition] = ';';
                    indexPosition++;
                }
            }
            return new Wrapper(EncryptedPublicKeysMessage.CODE, new EncryptedPublicKeysMessage(
                    Stream.of(Message.bytesToString(strings.get(6)).split("-")).map(Integer::parseInt).collect(Collectors.toList()),
                    certificates,
                    Message.bytesToBool(strings.get(4)),
                    Message.bytesToBool(strings.get(5)),
                    Message.bytesToString(strings.get(1)),
                    Message.bytesToInt(strings.get(2)),
                    Message.bytesToBool(strings.get(3))
            ));
        });
        all.put(AcknowledgePublicKeysMessage.CODE, strings -> {
            return new Wrapper(AcknowledgePublicKeysMessage.CODE, new AcknowledgePublicKeysMessage(
                    Message.bytesToString(strings.get(1)),
                    Message.bytesToInt(strings.get(2)),
                    Message.bytesToBool(strings.get(3))
            ));
        });
        return new WrapperFactory(all);
    }

    public static Map<Integer, MessageConsumer> initEventsBinding() {
        final Map<Integer, MessageConsumer> integerMessageConsumerMap = new HashMap<>();
        integerMessageConsumerMap.put(GetServerPublicKeysMessage.CODE, new GetServerPublicKeysMessageConsumer());
        integerMessageConsumerMap.put(EncryptedPublicKeysMessage.CODE, new EncryptedPublicKeysMessageConsumer());
        integerMessageConsumerMap.put(AcknowledgePublicKeysMessage.CODE, new AcknowledgePublicKeysMessageConsumer());
        return integerMessageConsumerMap;
    }

    public static void registerEventsBinding(MessageConsumerManager messageConsumerManager) {
        messageConsumerManager.register(GetServerPublicKeysMessage.CODE, new GetServerPublicKeysMessageConsumer());
        messageConsumerManager.register(EncryptedPublicKeysMessage.CODE, new EncryptedPublicKeysMessageConsumer());
        messageConsumerManager.register(AcknowledgePublicKeysMessage.CODE, new AcknowledgePublicKeysMessageConsumer());
    }

}
