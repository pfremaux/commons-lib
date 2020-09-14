package commons.lib.server.socket;

import commons.lib.server.socket.secured.ContactRegistry;
import commons.lib.server.socket.secured.SecuredSocketInitializer;
import commons.lib.server.socket.secured.step1.GetServerPublicKeysMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Basic client for sending bytes.
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private final String hostnameServer;
    private final int portServer;
    private SocketChannel server;

    public Client(String hostnameServer, int portServer) {
        this.hostnameServer = hostnameServer;
        this.portServer = portServer;
    }

    public void secured() {
        CompletableFuture<String> future = new CompletableFuture<>();
        ContactRegistry.AWAITED_EVENT.put(AsyncEvent.ASK_SECURED, future);
        WrapperFactory wrapperFactory = SecuredSocketInitializer.init();
        MessageConsumerManager messageConsumer = new MessageConsumerManager(SecuredSocketInitializer.initEventsBinding());
        Server server = new Server(hostnameServer, portServer, 50, messageConsumer, wrapperFactory);
        final String pwd = "changeme"; // TODO change
        final String clientHostname = "localhost";
        final int portHostname = 1234;
        final GetServerPublicKeysMessage getServerPublicKeysMessage = new GetServerPublicKeysMessage(pwd, 1, clientHostname, portHostname, true);
        final Function<List<String>, Wrapper> listWrapperFunction = wrapperFactory.getFunctionMap().get(GetServerPublicKeysMessage.CODE);
        final Wrapper apply = listWrapperFunction.apply(Arrays.asList(getServerPublicKeysMessage.serializeStrings()));
        byte[] serialize = apply.serialize();
        try {
            connectSendClose(serialize);
            server.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectSendClose(byte[] data) throws IOException {
        final SocketChannel server = SocketChannel.open();
        final SocketAddress socketAddr = new InetSocketAddress(hostnameServer, portServer);
        server.connect(socketAddr);
        final CompletableFuture<String> futureSecuredCommunication = ContactRegistry.AWAITED_EVENT.get(AsyncEvent.ASK_SECURED);
        if (futureSecuredCommunication != null) {
            try {
                futureSecuredCommunication.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        // TODO encrypt data is required
        logger.info("Client : sending {}", new String(data, StandardCharsets.UTF_8));
        server.write(ByteBuffer.wrap(data));
        logger.info("Data Sent");
        server.close();
    }

    public void connect() throws IOException {
        server = SocketChannel.open();
        SocketAddress socketAddr = new InetSocketAddress(hostnameServer, portServer);
        server.connect(socketAddr);
    }

    public void send(byte[] data) throws IOException {
        logger.info("Client : sending {}", new String(data, StandardCharsets.UTF_8));
        server.write(ByteBuffer.wrap(data));
    }

    public void disconnect() throws IOException {
        server.close();
    }

    public static SocketChannel connect(String hostnameServer, int portServer) throws IOException {
        SocketChannel server = SocketChannel.open();
        SocketAddress socketAddr = new InetSocketAddress(hostnameServer, portServer);
        server.connect(socketAddr);
        return server;
    }

    public static void send(SocketChannel server, byte[] data) throws IOException {
        server.write(ByteBuffer.wrap(data));
    }

    public static void disconnect(SocketChannel server) throws IOException {
        server.close();
    }
}
