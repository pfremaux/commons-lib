package commons.lib.extra.server.http.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import commons.lib.extra.server.http.HttpContext;
import commons.lib.extra.server.http.handler.testLib.DocumentedEndpoint;
import commons.lib.main.os.LogUtils;

public class SelfDescribeHandler implements HttpHandler {

    private static final Logger logger = LogUtils.initLogs();
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

    public SelfDescribeHandler(List<DocumentedEndpoint> endpointsDoc) {
        final StringBuilder builder = new StringBuilder();
        for (DocumentedEndpoint doc : endpointsDoc) {
            builder.append("<h1>");
            builder.append(doc.getMethod());
            builder.append(" ");
            builder.append(doc.getPath());
            builder.append("</h1>");
            builder.append("<p>");
            builder.append(doc.getDescription());
            builder.append("</p>");
            builder.append("<p>");
            builder.append(doc.getBodyExample());
            builder.append("</p>");
            builder.append("<p>");
            builder.append(doc.getResponseExample());
            builder.append("</p>");
            builder.append("<br/>");
        }
        this.strContexts = builder.toString();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.warning("Self describe");
        exchange.sendResponseHeaders(200, strContexts.length());
        final OutputStream os = exchange.getResponseBody();
        os.write(strContexts.getBytes());
        os.close();
    }
}
