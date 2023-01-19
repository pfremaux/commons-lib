package commons.lib.extra.server.http.handler.testLib;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import commons.lib.extra.server.http.Mapping;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Process {

    public static void handleException(HttpExchange exchange, Throwable t) throws IOException {

        final String msg = t.getLocalizedMessage() + "\n" + Arrays.stream(t.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
        exchange.sendResponseHeaders(400, msg.length());
        final OutputStream os = exchange.getResponseBody();
        os.write(msg.getBytes());
        os.close();
        exchange.getResponseBody().close();
    }


    public static void main(String[] args) throws IOException {
        final MyEndpoints instanceToProcess = new MyEndpoints();
        final Map<String, HttpHandler> handlers = loadHttpHandlers(instanceToProcess);
        final HttpServer server = HttpServer.create(new InetSocketAddress(8966), 0); // initSSL(port); //
        server.setExecutor(Executors.newFixedThreadPool(5));
        handlers.forEach(server::createContext);
        server.start();
    }

    private static Map<String, HttpHandler> loadHttpHandlers(MyEndpoints instanceToProcess) {
        final Map<String, HttpHandler> handlers = new HashMap<>();
        for (Method declaredMethod : instanceToProcess.getClass().getDeclaredMethods()) {
            Endpoint declaredAnnotation = declaredMethod.getDeclaredAnnotation(Endpoint.class);
            if (declaredAnnotation != null) {
                final String method = declaredAnnotation.method();
                final String path = declaredAnnotation.path();
                final Parameter parameter = Stream.of(declaredMethod.getParameters()).filter((Parameter p) -> !p.getType().equals(Map.class)).findFirst().orElse(null);
                if (parameter == null) {
                    throw new NullPointerException("body attribute of @Endpoint is null for method '" + declaredMethod.getName() + "'");
                }
                final HttpHandler handler = exchange -> {
                    if (!exchange.getRequestMethod().equals(method)) {
                        final String msg = "Method not allowed";
                        exchange.sendResponseHeaders(409, msg.length());
                        final OutputStream os = exchange.getResponseBody();
                        os.write(msg.getBytes());
                        os.close();
                        exchange.getResponseBody().close();
                        return;
                    }
                    final Map<String, List<String>> headers = new HashMap<>();
                    for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
                        final String key = entry.getKey();
                        final List<String> value = entry.getValue();
                        headers.put(key, value);
                    }

                    final byte[] bytes = exchange.getRequestBody().readAllBytes();
                    final String data = new String(bytes, StandardCharsets.UTF_8);

                    final Object b;
                    try {
                        b = Mapping.trivialJsonMappingV2(new StringBuilder(data), parameter.getType());
                        System.out.println(b.toString());
                    } catch (NoSuchFieldException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        handleException(exchange, e);
                        return;
                    }

                    try {
                        final Object result = declaredMethod.invoke(instanceToProcess, headers, b);
                        final OutputStream os = exchange.getResponseBody();
                        final String responseText;
                        if (result != null) {
                            responseText = Mapping.objectToJson(result).toString();
                        } else {
                            responseText = "{}";
                        }
                        exchange.sendResponseHeaders(200, responseText.length());
                        os.write(responseText.getBytes());
                        os.close();
                        exchange.getResponseBody().close();
                        return;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        handleException(exchange, e);
                        return;
                    }
                };
                handlers.put(path, handler);
            }
        }
        return handlers;
    }

}
