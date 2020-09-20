package commons.lib.server.socket;

import commons.lib.SystemUtils;
import commons.lib.security.asymetric.AsymmetricKeyHandler;
import commons.lib.security.asymetric.PrivateKeyHandler;
import commons.lib.security.asymetric.PublicKeyHandler;
import commons.lib.server.socket.secured.ContactRegistry;
import commons.lib.server.socket.secured.SecuredSocketInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.function.Function;

/**
 * This is a first version with only one command for the input and output. But later I'll need a list of these actions
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    protected final String hostname;
    protected final int port;
    protected int listenLimit;
    protected MessageConsumerManager messageConsumerManager;
    protected final WrapperFactory wrapperFactory;

    /**
     * @param hostname               Hostname of this server.
     * @param port                   Port of this server.
     * @param listenLimit            Number of call the server should listen before shutting down. Set 0 or less for infinite listen.
     * @param messageConsumerManager Message consumer for any message the server will receive.
     * @param wrapperFactory         Factory that contains wrapper builder.
     */
    public Server(String hostname,
                  int port,
                  int listenLimit,
                  MessageConsumerManager messageConsumerManager,
                  WrapperFactory wrapperFactory) {
        this.hostname = hostname;
        this.port = port;
        this.messageConsumerManager = messageConsumerManager;
        this.wrapperFactory = SecuredSocketInitializer.init(wrapperFactory);
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
        ServerSocket socket = serverSocket.socket();
        socket.bind(new InetSocketAddress(hostname, port));
        int listenCount = 0;
        do {
            inputClient = serverSocket.accept();
            final SocketAddress remoteAddress = inputClient.getRemoteAddress();
            InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteAddress;
            final String callerHostname = inetSocketAddress.getHostName();
            logger.info("Connection Set:  {}", remoteAddress);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            logger.info("Entering loop");
            while (inputClient.read(buffer) > 0) {
                logger.info("loop");
                buffer.flip();
                byteArrayOutputStream.write(buffer.array());
                buffer.clear();
            }
            logger.info("Size of the bytes received = {}", byteArrayOutputStream.size());
            final byte[] allDatum = byteArrayOutputStream.toByteArray();
            Wrapper inputWrapper = getWrapper(callerHostname, allDatum);
            byteArrayOutputStream.close();
            Message message = inputWrapper.getDatum();
            final int action = inputWrapper.getAction();
            logger.info("Key action {}", action);
            MessageConsumer messageConsumer = messageConsumerManager.getEventsLogic().get(action);
            logger.info("Class {}", messageConsumer.getClass());
            Optional<Wrapper> outputWrapper = messageConsumer.process(inputWrapper, hostname, port);
            if (outputWrapper.isPresent() && message.isRequireResponse()) {
                byte[] response = outputWrapper.get().serialize();
                logger.info("Responding {} with action {}", new String(response, StandardCharsets.UTF_8), outputWrapper.get().getAction());
                final String responseHostname = message.getResponseHostname();
                final int responsePort = message.getResponsePort();
                SocketChannel outputClient = Client.connect(responseHostname, responsePort);
                byte[] encryptedMaybeData = encryptMaybe(responseHostname, response);
                Client.send(outputClient, encryptedMaybeData);
                Client.disconnect(outputClient);
            } else {
                logger.info("Response not required");
            }
            listenCount++;
            logger.info("Listen count = {}. Ending when {}", listenCount, listenLimit);
            inputClient.close();
        } while (listenCount < listenLimit);
        socket.close();
        serverSocket.close();
    }

    protected byte[] encryptMaybe(String hostname, byte[] response) {
        final PublicKeyHandler publicKeyHandler = new PublicKeyHandler(); // TODO as attribute
        if (ContactRegistry.TRUSTED.contains(hostname)) {
            List<PublicKey> publicKeys = ContactRegistry.getPublicKeys(hostname);
            try {
                return publicKeyHandler.recursiveProcessor(new LinkedList<>(publicKeys), AsymmetricKeyHandler.toBufferedInputStream(response)).readAllBytes();
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                e.printStackTrace();
                SystemUtils.failProgrammer();
                return new byte[0];
            }
        } else {
            return response;
        }
    }

    protected Wrapper getWrapper(String callerHostname, byte[] allDatum) {
        byte[] deciphered = new byte[0];

        if (ContactRegistry.TRUSTED.contains(callerHostname)) {
            List<PrivateKey> privateKeys = ContactRegistry.getPrivateKeys(hostname);
            PrivateKeyHandler privateKeyHandler = new PrivateKeyHandler(); // TODO as attribute
            try {
                // TODO validate this really work
                deciphered = privateKeyHandler.recursiveProcessor(new LinkedList<>(privateKeys), AsymmetricKeyHandler.toBufferedInputStream(allDatum)).readAllBytes();
            } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                e.printStackTrace();
                SystemUtils.failProgrammer();
            }
        } else {
            deciphered = allDatum;
        }

        logger.info("deciphered received : {}", Message.bytesToString(deciphered));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<byte[]> result = new ArrayList<>();
        for (byte b : deciphered) {
            if (b == ';') {
                result.add(outputStream.toByteArray());
                outputStream = new ByteArrayOutputStream();
            } else {
                outputStream.write(b);
            }
        }
        result.add(outputStream.toByteArray());

        for (byte[] bytes : result) {
            logger.info("once stored : {}", Message.bytesToString(bytes));
        }
        final int action = Message.bytesToInt(result.get(0));
        final Map<Integer, Function<List<byte[]>, Wrapper>> functionMap = wrapperFactory.getFunctionMap();
        logger.info("Current wrapper factory size = {}, with {}", functionMap.size(), functionMap.keySet());
        for (Map.Entry<Integer, Function<List<byte[]>, Wrapper>> entry : functionMap.entrySet()) {
            logger.info("{} -> {}", entry.getKey(), entry.getValue());
        }
        logger.info("Searching action {}", action);
        final Function<List<byte[]>, Wrapper> listWrapperFunction = functionMap.get(action);
        logger.info("Wrapper consumer found ? = {}", listWrapperFunction);

        return listWrapperFunction.apply(result);
    }

}
