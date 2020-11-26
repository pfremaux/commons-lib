package commons.lib.server.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class DefaultRootHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String response = "<h1>Default root handler of http server</h1>";
        exchange.sendResponseHeaders(200, response.length());
        final OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
