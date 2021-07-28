package commons.lib.extra.server.socket;

import commons.lib.extra.security.asymetric.AsymmetricKeyHandler;
import commons.lib.extra.security.asymetric.PublicKeyHandler;
import commons.lib.extra.security.symetric.SymmetricHandler;
import commons.lib.extra.server.socket.secured.ContactRegistry;
import commons.lib.extra.server.socket.secured.SecuredSocketInitializer;
import commons.lib.extra.server.socket.secured.step1.GetServerPublicKeysMessage;
import commons.lib.main.os.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Basic client for sending bytes.
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private final String hostnameServer;
    private final int portServer;
    private SocketChannel server;
    private PublicKeyHandler publicKeyHandler = null;

    public Client(String hostnameServer, int portServer) {
        this.hostnameServer = hostnameServer;
        this.portServer = portServer;
    }

    public void secured(String clientHostname, int clientPort) {
        LogUtils.debug("Starting handshake from me {}:{} to distant entity {}:{}", clientHostname, clientPort, hostnameServer, portServer);
        if (ContactRegistry.TRUSTED.contains(hostnameServer)) {
            LogUtils.debug("{} already trusted, not trying to handshake again.", hostnameServer);
            return;
        }
        final CompletableFuture<String> future = new CompletableFuture<>();
        ContactRegistry.AWAITED_EVENT.put(AsyncEvent.ASK_SECURED, future);
        final WrapperFactory wrapperFactory = SecuredSocketInitializer.init();
        final MessageConsumerManager messageConsumer = new MessageConsumerManager(SecuredSocketInitializer.initEventsBinding());
        final Server clientAsServerForHandshake = new Server(clientHostname, clientPort, 2, messageConsumer, wrapperFactory);
        final String pwd = "changeme"; // TODO change
        LogUtils.debug("Building message...");
        final GetServerPublicKeysMessage getServerPublicKeysMessage = new GetServerPublicKeysMessage(pwd, 1, clientHostname, clientPort, true);
        ContactRegistry.storeSymmetricKey(hostnameServer, SymmetricHandler.getKey(pwd, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO));
        final Function<List<byte[]>, Wrapper> listWrapperFunction = wrapperFactory.getFunctionMap().get(GetServerPublicKeysMessage.CODE);
        LogUtils.debug("Serializing data...");
        final List<byte[]> serializedDataAsStrings = Stream.concat(Stream.of(Message.intToBytes(GetServerPublicKeysMessage.CODE)), Stream.of(getServerPublicKeysMessage.serializeBytes())).collect(Collectors.toList());
        final Wrapper apply = listWrapperFunction.apply(serializedDataAsStrings);
        byte[] serialize = apply.serialize();
        try {
            LogUtils.debug("Sending serialized data...");
            connect();
            justSend(serialize);
            disconnect();
            LogUtils.debug("Listening for handshake.");
            clientAsServerForHandshake.listen();
            LogUtils.debug("Listening ended");
            publicKeyHandler = new PublicKeyHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectSendClose(byte[] data) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        final SocketChannel connectionToServer = SocketChannel.open();
        final SocketAddress socketAddr = new InetSocketAddress(hostnameServer, portServer);
        connectionToServer.connect(socketAddr);
        final CompletableFuture<String> futureSecuredCommunication = ContactRegistry.AWAITED_EVENT.get(AsyncEvent.ASK_SECURED);
        if (futureSecuredCommunication != null) {
            try {
                futureSecuredCommunication.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        byte[] dataToSend;
        if (publicKeyHandler != null) {
            List<PublicKey> publicKeys = ContactRegistry.getPublicKeys(hostnameServer);
            dataToSend = publicKeyHandler.recursiveProcessor(new LinkedList<>(publicKeys), AsymmetricKeyHandler.toBufferedInputStream(data)).readAllBytes();
        } else {
            dataToSend = data;
        }
        LogUtils.debug("sending {}", new String(dataToSend, StandardCharsets.UTF_8));
        connectionToServer.write(ByteBuffer.wrap(dataToSend));
        LogUtils.debug("Data Sent");
        connectionToServer.close();
    }

    public void connect() throws IOException {
        server = SocketChannel.open();
        SocketAddress socketAddr = new InetSocketAddress(hostnameServer, portServer);
        server.connect(socketAddr);
    }

    public void justSend(byte[] data) throws IOException {
        LogUtils.debug("Client : sending {}", new String(data, StandardCharsets.UTF_8));
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
