package commons.lib.server.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * This is a first version with only one command for the input and output. But later I'll need a list of these actions
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final String hostname;
    private final int port;
    private int listenLimit;
    private MessageConsumerManager messageConsumerManager;
    private final WrapperFactory wrapperFactory;

    /**
     * @param hostname               Hostname of this server.
     * @param port                   Port of this server.
     * @param listenLimit            Number of call the server should listen before shutting down. Set 0 or less for infinite listen.
     * @param messageConsumerManager Message consumer for any message the server will receive.
     * @param wrapperFactory
     */
    public Server(String hostname,
                  int port,
                  int listenLimit,
                  MessageConsumerManager messageConsumerManager,
                  WrapperFactory wrapperFactory) {
        this.hostname = hostname;
        this.port = port;
        this.messageConsumerManager = messageConsumerManager;
        this.wrapperFactory = wrapperFactory;
        this.listenLimit = listenLimit <= 0 ? Integer.MAX_VALUE : listenLimit;

    }

    /**
     * Start to listen to the port defined in the constructor.
     *
     * @throws IOException
     */
    public void listen() throws IOException {
        SocketChannel inputClient;
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        logger.info("Listening on port {}", port);
        serverSocket.socket().bind(new InetSocketAddress(hostname, port));
        int listenCount = 0;
        do {
            inputClient = serverSocket.accept();
            final SocketAddress remoteAddress = inputClient.getRemoteAddress();
            logger.info("Connection Set:  {}", remoteAddress);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (inputClient.read(buffer) > 0) {
                buffer.flip();
                byteArrayOutputStream.write(buffer.array());
                buffer.clear();
            }
            logger.info("Size of the bytes received = {}", byteArrayOutputStream.size());
            final byte[] allDatum = byteArrayOutputStream.toByteArray();
            Wrapper inputWrapper = getWrapper(allDatum);
            byteArrayOutputStream.close();
            Message message = inputWrapper.getDatum();
            final int action = inputWrapper.getAction();
            logger.info("Key action {}", action);
            MessageConsumer messageConsumer = messageConsumerManager.getEventsLogic().get(action);
            logger.info("Class {}", messageConsumer.getClass());
            Optional<Wrapper> outputWrapper = messageConsumer.process(inputWrapper, hostname, port);
            if (outputWrapper.isPresent() && message.isRequireResponse()) {
                byte[] response = outputWrapper.get().serialize();
                logger.info("Responding {}", new String(response, StandardCharsets.UTF_8));
                final String responseHostname = message.getResponseHostname();
                final int responsePort = message.getResponsePort();
                // TODO should be in the client call ? Think about it. A server is a client if he decides to call by himself
                SocketChannel outputClient = Client.connect(responseHostname, responsePort);
                Client.send(outputClient, response);
                Client.disconnect(outputClient);
            } else {
                logger.info("Response not required");
            }
            listenCount++;
            logger.info("Listen count = {}. Ending when {}", listenCount, listenLimit);
            inputClient.close();
        } while (listenCount < listenLimit);
    }

    private Wrapper getWrapper(byte[] allDatum) {
        // TODO deserialization secured
        final String data = new String(allDatum, StandardCharsets.UTF_8).trim();
        logger.info("Data received : {}", data);
        final String[] list = data.split(";");
        final String action = list[0];
        final Map<Integer, Function<List<String>, Wrapper>> functionMap = wrapperFactory.getFunctionMap();
        logger.info("Current wrapper factory size = {}, with {}", functionMap.size(), functionMap.keySet());
        for (Map.Entry<Integer, Function<List<String>, Wrapper>> entry : functionMap.entrySet()) {
            logger.info("{} -> {}", entry.getKey(), entry.getValue());
        }
        logger.info("Searching action {}", action);
        final Function<List<String>, Wrapper> listWrapperFunction = functionMap.get(Integer.parseInt(action));
        logger.info("Wrapper consumer found ? = {}", listWrapperFunction);
        return listWrapperFunction.apply(Arrays.asList(list));
    }

}
