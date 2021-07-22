package commons.lib.extra.server.http.config;

import com.sun.net.httpserver.HttpHandler;
import commons.lib.extra.server.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ServerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ServerConfiguration.class);

    public static final String SERVER_TOOLBOX_PROPERTIES = "server-toolbox.properties";
    public static final String SERVER_PROPERTIES = "server.properties";

    public static List<HttpContext> loadConfig() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Map<String, Class<HttpHandler>> externalHandlers = new HashMap<>();
        final Properties properties = loadHandlersDefinition(externalHandlers);

        final List<HttpContext> httpContexts = new ArrayList<>();
        logger.debug("Loading {}", SERVER_PROPERTIES);
        try (FileInputStream in = new FileInputStream(SERVER_PROPERTIES)) {
            properties.load(in);
            boolean loop = true;
            for (int i = 0; loop; i++) {
                final String relativeUrlPath = properties.getProperty(String.format("context.%d.path.web", i));
                if (relativeUrlPath == null) {
                    loop = false;
                    continue;
                }
                final String description = properties.getProperty(String.format("context.%d.description", i));
                boolean readNextParameter = true;
                List<String> parameters = new ArrayList<>();
                for (int j = 0; readNextParameter; j++) {
                    final String parameter = properties.getProperty(String.format("context.%d.path.parameter.%d", i, j));
                    if (parameter == null) {
                        readNextParameter = false;
                        continue;
                    }
                    parameters.add(parameter);
                }
                final String handler = properties.getProperty(String.format("context.%d.handler", i));
                Class<HttpHandler> httpHandlerClass = externalHandlers.get(handler);
                Constructor<HttpHandler> constructor = httpHandlerClass.getConstructor(List.class);
                HttpHandler httpHandler = constructor.newInstance(parameters);
                httpContexts.add(new HttpContext(relativeUrlPath, httpHandler, description));
            }
        }
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            System.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }
        return httpContexts;
    }

    private static Properties loadHandlersDefinition(Map<String, Class<HttpHandler>> externalHandlers) throws IOException, ClassNotFoundException {
        Properties properties = new Properties();
        logger.debug("Loading {}", SERVER_TOOLBOX_PROPERTIES);
        try (FileInputStream in = new FileInputStream(SERVER_TOOLBOX_PROPERTIES)) {
            properties.load(in);
            boolean loop = true;
            for (int i = 0; loop; i++) {
                final String id = properties.getProperty(String.format("handler.%d.id", i));
                if (id == null) {
                    loop = false;
                    continue;
                }
                final String pack = properties.getProperty(String.format("handler.%d.package", i));
                externalHandlers.put(id, getHttpHandler(pack));
            }
        }
        return properties;
    }


    @SuppressWarnings(value = "unchecked")
    private static Class<HttpHandler> getHttpHandler(String pack) throws ClassNotFoundException {
        return (Class<HttpHandler>) Class.forName(pack);
    }

}
