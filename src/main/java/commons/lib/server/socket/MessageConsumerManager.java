package commons.lib.server.socket;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains all message consumer that a Client/Server should know in
 * order to consumer any message from a Client/Server.
 *
 * @see MessageConsumer
 */
public class MessageConsumerManager {
    private final Map<Integer, MessageConsumer> eventsLogic;

    public MessageConsumerManager() {
        eventsLogic = new HashMap<>();
    }

    public MessageConsumerManager(Map<Integer, MessageConsumer> eventsLogic) {
        this.eventsLogic = eventsLogic;
    }

    public void register(Integer key, MessageConsumer messageConsumer) {
        this.eventsLogic.put(key, messageConsumer);
    }


    public Map<Integer, MessageConsumer> getEventsLogic() {
        return eventsLogic;
    }
}
