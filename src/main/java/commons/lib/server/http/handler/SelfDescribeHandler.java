package commons.lib.server.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import commons.lib.server.http.HttpContext;

import java.io.IOException;
import java.io.OutputStream;

public class SelfDescribeHandler implements HttpHandler {

    private String strContexts;

    public SelfDescribeHandler(HttpContext[] contexts) {
        final StringBuilder builder = new StringBuilder();
        for (HttpContext context : contexts) {
            builder.append("<h1>");
            builder.append(context.getPath());
            builder.append("</h1>");
            builder.append("<p>");
            builder.append(context.getDescription());
            builder.append("</p>");
            builder.append("<p>");
            builder.append(context.getHandler().getClass().getSimpleName());
            builder.append("</p>");
            builder.append("<br/>");
        }
        this.strContexts = builder.toString();

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, strContexts.length());
        final OutputStream os = exchange.getResponseBody();
        os.write(strContexts.getBytes());
        os.close();
    }
}
