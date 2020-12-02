package commons.lib.extra.server.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import commons.lib.extra.server.InMemoryDb;
import commons.lib.extra.server.http.HttpServerException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class RestEntitiesHandler implements HttpHandler {

    private static Logger logger = Logger.getLogger(RestEntitiesHandler.class.getName());
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
        os.close();
        //exchange.getResponseBody().close();
    }

    @NotNull
    private StringBuilder processAndGetResponseBody(HttpExchange exchange, List<String> params) throws IOException, HttpServerException {
        String table = params.get(0);
        if (table.equals("meta")) {
            String whatMeta = params.get(1);
            if (whatMeta.equals("table")) {
                StringBuilder response = new StringBuilder();
                fillWithJsonFormat(response, inMemoryDb.tables());
                return response;
            }
            throw new HttpServerException("meta data unknown", "where does " + whatMeta + " comes from ?");
        }
        int id = -1;
        if (params.size() > 1) {
            id = Integer.parseInt(params.get(1));
        }
        String requestMethod = exchange.getRequestMethod();
        logger.warning("Method : " + requestMethod);
        StringBuilder builder = new StringBuilder();
        switch (requestMethod) {
            case "GET":
                if (params.size() == 1) {
                    List<Map<String, String>> list = inMemoryDb.list(table, 1, 100);
                    fillWithJsonFormat(builder, list);
                } else {
                    Map<String, String> stringStringMap = inMemoryDb.get(table, id);
                    fillWithJsonFormat(builder, stringStringMap);
                }
                break;
            case "PUT":
                final byte[] bytes = exchange.getRequestBody().readAllBytes();
                String data = new String(bytes, StandardCharsets.UTF_8);
                Map<String, String> map = trivialJsonMapping(data);
                inMemoryDb.update(table, id, map);
                break;
            case "POST":
                final byte[] bytes2 = exchange.getRequestBody().readAllBytes();
                String data2 = new String(bytes2, StandardCharsets.UTF_8);
                Map<String, String> map2 = trivialJsonMapping(data2);
                int newId = inMemoryDb.add(table, map2);
                fillWithJsonFormat(builder, Collections.singletonMap("id", Integer.toString(newId)));
                break;
            default:
                exchange.sendResponseHeaders(429, 0);
                exchange.getResponseBody().close();
        }
        return builder;
    }

    private Map<String, String> trivialJsonMapping(String data) {
        Map<String, String> map = new HashMap<>();
        // Yes this is trivial
        String cleanedJson = data
                .replaceAll("\\{", "")
                .replaceAll("\\}", "")
                .replaceAll("\"", "");
        StringTokenizer tokenizer1 = new StringTokenizer(cleanedJson, ",");
        while (tokenizer1.hasMoreTokens()) {
            String element = tokenizer1.nextToken();
            String[] split = element.split(":");
            map.put(split[0], split[1]);
        }
        return map;
    }

    private void fillWithJsonFormat(StringBuilder builder, Collection<String> list) {
        builder.append("[");
        for (String string : list) {
            builder.append("\"");
            builder.append(string);
            builder.append("\"");
            builder.append(",");
        }
        if (list.size() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
    }

    private void fillWithJsonFormat(StringBuilder builder, List<Map<String, String>> list) {
        builder.append("[");
        for (Map<String, String> stringStringMap : list) {
            fillWithJsonFormat(builder, stringStringMap);
            builder.append(",");
        }
        if (list.size() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
    }

    private void fillWithJsonFormat(StringBuilder builder, Map<String, String> stringStringMap) {
        builder.append("{");
        for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
            builder.append("\"");
            builder.append(entry.getKey());
            builder.append("\":\"");
            builder.append(entry.getValue());
            builder.append("\"");
            builder.append(",");
        }
        if (!stringStringMap.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}");
    }
}
