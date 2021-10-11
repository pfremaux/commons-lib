package commons.lib.extra.server.http.handler.auth;

import com.sun.net.httpserver.HttpExchange;
import commons.lib.extra.server.http.handler.auth.pojo.DefaultTokenFields;
import commons.lib.extra.server.http.handler.auth.pojo.Token;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TestAuthenticatedHandler extends AuthenticatedHandler {
    public TestAuthenticatedHandler(List<String> params) {
        super(params.get(0), DefaultTokenFields.valueOf(params.get(1)));
    }

    @Override
    public void handleAuthenticated(Token token, HttpExchange exchange) throws IOException {
        String msg = "CA MARCHE";
        System.out.println("authentification passée");
        exchange.sendResponseHeaders(200, msg.length());
        final OutputStream os = exchange.getResponseBody();
        os.write(msg.getBytes());
        os.close();
    }
}
