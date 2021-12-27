package commons.lib.extra.server.http;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import commons.lib.extra.server.http.config.ServerConfiguration;
import commons.lib.extra.server.http.handler.DefaultRootHandler;
import commons.lib.extra.server.http.handler.SelfDescribeHandler;
import commons.lib.main.os.LogUtils;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Http {

    private static final Logger logger = LogUtils.initLogs();

    public static final String PRIVATE_SELF_DESCRIBE_PATH = "/private/selfDescribe/";

    private static HttpServer server;

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        System.setProperty("console.encoding", "UTF-8");
        System.setProperty("file.encoding", "UTF-8");
        startServer();
    }
// TODO PFR HTTPS :  https://www.codeproject.com/Tips/1043003/Create-a-Simple-Web-Server-in-Java-HTTPS-Server
    public static void startServer() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final List<HttpContext> httpContexts = ServerConfiguration.loadConfig();
        final String listeningPort = System.getProperty("server.listening");
        int port = Integer.parseInt(listeningPort);
        Http.startServer(port, httpContexts.toArray(new HttpContext[0]));
    }


    public static void startServer(int port, HttpContext... contexts) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0); // initSSL(port); //
        if (contexts.length == 0) {
            LogUtils.debug("No handler set. Loading the default handler...");
            server.createContext("/", new DefaultRootHandler());
        } else {
            for (HttpContext context : contexts) {
                server.createContext(context.getPath(), context.getHandler());
            }
        }
        server.setExecutor(Executors.newFixedThreadPool(5));
        server.createContext(PRIVATE_SELF_DESCRIBE_PATH, new SelfDescribeHandler(contexts));
        LogUtils.debug("Self description available here : http://127.0.0.1:{}{}", port, PRIVATE_SELF_DESCRIBE_PATH);
        LogUtils.debug("server listening port {}", port);
        server.start();
    }

    private static HttpsServer initSSL(int port) {
        try {
            logger.warning("init ssl");
            // Set up the socket address
            InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), port);

            // Initialise the HTTPS server
            HttpsServer httpsServer = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // Initialise the keystore
            char[] password = "simulator".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("server.keystore");
            ks.load(fis, password);

            // Set up the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // Set up the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            // Set up the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        // Initialise the SSL context
                        SSLContext c = SSLContext.getDefault();
                        SSLEngine engine = c.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        // Get the default parameters
                        SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
                        params.setSSLParameters(defaultSSLParameters);
                    } catch (Exception ex) {
                        logger.warning("Failed to create HTTPS port");
                        logger.throwing(Http.class.getSimpleName(), "initSSL", ex);
                    }
                }
            });
            return httpsServer;
        } catch (Exception exception) {
            logger.warning("Failed to create HTTPS server on port " + port + " of localhost");
            logger.throwing(Http.class.getSimpleName(), "initSSL", exception);
        }
        return null;
    }

    public static void stopServer() {
        server.stop(5);
    }

}
