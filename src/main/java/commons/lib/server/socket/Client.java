package commons.lib.server.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

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

    public void connectSendClose(byte[] data) throws IOException {
        SocketChannel server = SocketChannel.open();
        SocketAddress socketAddr = new InetSocketAddress(hostnameServer, portServer);
        server.connect(socketAddr);
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
