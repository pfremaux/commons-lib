package commons.lib.extra.server.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import commons.lib.extra.server.InMemoryDb;
import commons.lib.extra.server.http.HttpServerException;
import commons.lib.extra.server.http.Mapping;
import commons.lib.main.os.LogUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

public class RestEntitiesHandler implements HttpHandler {

    private static Logger logger = Logger.getLogger(RestEntitiesHandler.class.getName());
    private Path DIRECTORY_DATA = Path.of(System.getProperty("in.memory.data", ".\\"));
    ///private static final Logger logger = LoggerFactory.getLogger(RestEntitiesHandler.class);

    private final InMemoryDb inMemoryDb = new InMemoryDb();
    private final String baseUrlPath;

    public RestEntitiesHandler(List<String> parameters) {
        this.baseUrlPath = parameters.get(0);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final URI requestURI = exchange.getRequestURI();
        logger.warning("OK");
        logger.warning("Requested URI : " + requestURI.getPath());
        String path = requestURI.getPath();
        String substring1 = path.substring(baseUrlPath.length());
        logger.warning("Looking for entities in : " + substring1);
        StringTokenizer tokenizer = new StringTokenizer(substring1, "/");
        List<String> params = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            params.add(tokenizer.nextToken());
        }

        StringBuilder builder = null;
        try {
            builder = processAndGetResponseBody(exchange, params);
        } catch (HttpServerException e) {
            e.printStackTrace();
        }
        //byte[] bytes = Files.readAllBytes(filePath);
        String responseBody = builder.toString();
        logger.warning("Response size : " + responseBody.length());
        exchange.sendResponseHeaders(200, responseBody.length());
        final OutputStream os = exchange.getResponseBody();
        os.write(responseBody.getBytes());
        os.flush();
        os.close();
        //exchange.getResponseBody().close();
    }

    private StringBuilder processAndGetResponseBody(HttpExchange exchange, List<String> params) throws IOException, HttpServerException {
        String table = params.get(0);
        if (table.equals("meta")) {
            String whatMeta = params.get(1);
            if (whatMeta.equals("table")) {
                StringBuilder response = new StringBuilder();
                Mapping.fillWithJsonFormat(response, inMemoryDb.tables());
                return response;
            } else if (whatMeta.equals("file")) {
                StringBuilder builder = new StringBuilder();
                String action = params.get(2);
                if ("save".equals(action)) {
                    LogUtils.debug("Saving...");
                    inMemoryDb.save(DIRECTORY_DATA);
                    return builder;
                } else if ("load".equals(action)) {
                    LogUtils.debug("Loading...");
                    inMemoryDb.loadAll(DIRECTORY_DATA);
                    return builder;
                } else if ("clear".equals(action)) {
                    LogUtils.debug("Clearing...");
                    inMemoryDb.clearAll();
                    return builder;
                }
            }
            throw new HttpServerException("meta data unknown", "where does " + whatMeta + " comes from ?");
        }
        int id = -1;
        if (params.size() > 1) {
            id = Integer.parseInt(params.get(1)) - 1;
        }
        String requestMethod = exchange.getRequestMethod();
        logger.warning("Method : " + requestMethod);
        StringBuilder builder = new StringBuilder();
        switch (requestMethod) {
            case "GET":
                if (params.size() == 1) {
                    List<Map<String, String>> list = inMemoryDb.list(table, 1, 100);
                    Mapping.fillWithJsonFormat(builder, list);
                } else {
                    Map<String, String> stringStringMap = inMemoryDb.get(table, id);
                    Mapping.fillWithJsonFormat(builder, stringStringMap);
                }
                break;
            case "PUT":
                final byte[] bytes = exchange.getRequestBody().readAllBytes();
                String data = new String(bytes, StandardCharsets.UTF_8);
                Map<String, String> map = Mapping.trivialJsonMapping(data);
                inMemoryDb.update(table, id, map);
                break;
            case "POST":
                final byte[] bytes2 = exchange.getRequestBody().readAllBytes();
                String data2 = new String(bytes2, StandardCharsets.UTF_8);
                Map<String, String> map2 = Mapping.trivialJsonMapping(data2);
                int newId = inMemoryDb.add(table, map2);
                Mapping.fillWithJsonFormat(builder, Collections.singletonMap("id", Integer.toString(newId)));
                break;
            default:
                exchange.sendResponseHeaders(429, 0);
                exchange.getResponseBody().close();
        }
        return builder;
    }


}
