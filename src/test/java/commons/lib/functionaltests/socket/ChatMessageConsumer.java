package commons.lib.functionaltests.socket;

import commons.lib.extra.server.socket.MessageConsumer;
import commons.lib.extra.server.socket.Wrapper;
import commons.lib.functionaltests.socket.unsecured.UnsecuredChat;
import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.CustomConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ChatMessageConsumer implements MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageConsumer.class);

    @Override
    public Optional<Wrapper> process(Wrapper input, String consumerHostname, int consumerPort) {
        logger.debug("Starting chat message consumer");
        final ChatMessage chatMessage = (ChatMessage) input.getDatum();
        final CustomConsole instance = ConsoleFactory.getInstance();
        String message = chatMessage.getMessage();
        logger.debug("message received was : {}", message);
        instance.printf("Received and consuming : %s", message);
        String responseMessage;
        instance.printf("Message received : %s", message);
        if (consumerPort == 11111) {
            responseMessage = UnsecuredChat.CLIENT_2_TO_CLIENT_1.get(message);
        } else {
            responseMessage = UnsecuredChat.CLIENT_1_TO_CLIENT_2.get(message);
        }
        if (responseMessage == null) {
            instance.printf("No response available");
            return Optional.empty();
        }
        instance.printf("Will answer %s", responseMessage);
        final ChatMessage response = new ChatMessage(responseMessage, consumerHostname, consumerPort, true);
        return Optional.of(new Wrapper(UnsecuredChat.CHAT_MESSAGE_CODE, response));
    }
}
