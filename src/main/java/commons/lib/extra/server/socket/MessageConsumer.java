package commons.lib.extra.server.socket;

import java.util.Optional;

/**
 * This interface should be implemented by any class that has to consumer a message
 * send by a Client/Server.
 */
public interface MessageConsumer {

    /**
     * Consumes a message and returns (optionally) what the called service should respond.
     *
     * @param input            The data received by the caller you need to process.
     * @param consumerHostname Hostname of the consumer. It's useful when this consumer asked for an answer.
     * @param consumerPort Port of the consumer. It's useful when this consumer asked for an answer.
     * @return A Wrapper that should be returned to the caller if it exists.
     * @see Wrapper
     */
    Optional<Wrapper> process(Wrapper input, String consumerHostname, int consumerPort);

}
