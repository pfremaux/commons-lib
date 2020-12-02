package commons.lib.extra.server.http;

import com.sun.net.httpserver.HttpServer;
import commons.lib.extra.server.http.config.ServerConfiguration;
import commons.lib.extra.server.http.handler.DefaultRootHandler;
import commons.lib.extra.server.http.handler.SelfDescribeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.List;

public class Http {

    private static final Logger logger = LoggerFactory.getLogger(Http.class);

    public static final String PRIVATE_SELF_DESCRIBE_PATH = "/private/selfDescribe/";

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        startServer();
    }

    public static void startServer() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final List<HttpContext> httpContexts = ServerConfiguration.loadConfig();
        final String listeningPort = System.getProperty("server.listening");
        int port = Integer.parseInt(listeningPort);
        Http.startServer(port, httpContexts.toArray(new HttpContext[0]));
    }


    public static void startServer(int port, HttpContext... contexts) throws IOException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        if (contexts.length == 0) {
            logger.info("No handler set. Loading the default handler...");
            server.createContext("/", new DefaultRootHandler());
        } else {
            for (HttpContext context : contexts) {
                server.createContext(context.getPath(), context.getHandler());
            }
        }
        server.setExecutor(null);
        server.createContext(PRIVATE_SELF_DESCRIBE_PATH, new SelfDescribeHandler(contexts));
        logger.info("Self description available here : http://127.0.0.1:{}{}", port, PRIVATE_SELF_DESCRIBE_PATH);
        logger.info("server listening port {}", port);
        server.start();
    }


}
