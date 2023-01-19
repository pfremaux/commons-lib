package commons.lib.extra.server.http.handler.auth;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import commons.lib.extra.security.symetric.SymmetricHandler;
import commons.lib.extra.server.http.Mapping;
import commons.lib.extra.server.http.handler.auth.pojo.DefaultTokenFields;
import commons.lib.extra.server.http.handler.auth.pojo.Token;
import commons.lib.extra.server.http.handler.auth.pojo.TokenStructure;
import commons.lib.main.SystemUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class AuthenticationHandler implements HttpHandler {

    public static final String MSG_FAILED_TO_LOGIN = "{\"msg\":\"failed to login\"}";
    private final SecretKeySpec secret;
    private final TokenStructure tokenStructure = new TokenStructure(DefaultTokenFields.values());

    public AuthenticationHandler(List<String> params) {
        this.secret = SymmetricHandler.getKey(SymmetricHandler.fillPassword(params.get(0)), SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final Headers requestHeaders = exchange.getRequestHeaders();
        final InputStream requestBody = exchange.getRequestBody();
        final byte[] allBytes = requestBody.readAllBytes();
        final String bodyData = new String(allBytes, StandardCharsets.UTF_8);
        final Map<String, String> jsonData = Mapping.trivialJsonMapping(bodyData);
        final String login = jsonData.get("login");
        final String pass = jsonData.get("pwd");
        String userId = authenticate(login, pass);
        if (userId == null) {
            exchange.getResponseHeaders().add("Content-Type", "text/json");
            exchange.sendResponseHeaders(400, MSG_FAILED_TO_LOGIN.length());
            exchange.getResponseBody().write(MSG_FAILED_TO_LOGIN.getBytes(StandardCharsets.UTF_8));
            exchange.getResponseBody().close();
            return;
        }
        Token token = new Token();
        token.put(DefaultTokenFields.USER_ID, userId);
        token.put(DefaultTokenFields.VERSION, "0");
        token.put(DefaultTokenFields.EXPIRATION_TIMESTAMP_MS, Long.toString(System.currentTimeMillis() + Duration.ofHours(1L).toMillis()));
        byte[] encrypt = null;
        try {
            encrypt = SymmetricHandler.encrypt(secret, tokenStructure.getFormattedTokenInClear(token).getBytes(StandardCharsets.UTF_8), SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            SystemUtils.failSystem();
        }
        final byte[] base64Bytes = Base64.getEncoder().encode(encrypt);
        final String encryptedString = new String(base64Bytes);
        System.out.println(encryptedString);

        /*final Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("token", encryptedString);*/
        StringBuilder builder = new StringBuilder();

        Mapping.fillWithJsonFormat(builder, Map.of("token", encryptedString));
        String msg = builder.toString();
        //exchange.sendResponseHeaders(200, 0);
        //final OutputStream os = exchange.getResponseBody();
        //os.write(msg.getBytes());
        //os.close();

        exchange.getResponseHeaders().add("Content-Type", "text/json");
        exchange.sendResponseHeaders(200, msg.length());
        exchange.getResponseBody().write(msg.getBytes(StandardCharsets.UTF_8));
        exchange.getResponseBody().close();
    }


    private String authenticate(String login, String password) {
        // TODO AUTH DB
        if ("admin".equals(login) && "admin".equals(password)) {
            return Long.toString(1L);
        }
        return null;
    }

}
