package commons.lib.extra.server.socket.secured.step4;

import commons.lib.extra.server.socket.AsyncEvent;
import commons.lib.extra.server.socket.MessageConsumer;
import commons.lib.extra.server.socket.Wrapper;
import commons.lib.extra.server.socket.secured.ContactRegistry;

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
