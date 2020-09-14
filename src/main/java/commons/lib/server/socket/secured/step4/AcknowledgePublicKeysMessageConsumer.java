package commons.lib.server.socket.secured.step4;

import commons.lib.server.socket.AsyncEvent;
import commons.lib.server.socket.MessageConsumer;
import commons.lib.server.socket.Wrapper;
import commons.lib.server.socket.secured.ContactRegistry;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AcknowledgePublicKeysMessageConsumer implements MessageConsumer {

    @Override
    public Optional<Wrapper> process(Wrapper input, String consumerHostname, int consumerPort) {
        String responseHostname = input.getDatum().getResponseHostname();
        CompletableFuture<String> stringCompletableFuture = ContactRegistry.AWAITED_EVENT.get(AsyncEvent.ASK_SECURED);
        if (stringCompletableFuture != null) {
            stringCompletableFuture.complete(responseHostname);
        }
        return Optional.empty();
    }
}
