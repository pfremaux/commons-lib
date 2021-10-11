package commons.lib.extra.server.http.handler.auth;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import commons.lib.extra.security.symetric.SymmetricHandler;
import commons.lib.extra.server.http.handler.auth.pojo.DefaultTokenFields;
import commons.lib.extra.server.http.handler.auth.pojo.Token;
import commons.lib.extra.server.http.handler.auth.pojo.TokenField;
import commons.lib.extra.server.http.handler.auth.pojo.TokenStructure;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

public abstract class AuthenticatedHandler implements HttpHandler {

    private final SecretKeySpec key;
    private final SecretKeySpec secret;
    private final TokenField userIdField;
    private final TokenStructure tokenStructure = new TokenStructure(DefaultTokenFields.values());

    public AuthenticatedHandler(String pass, TokenField userIdField) {
        this.secret = SymmetricHandler.getKey(SymmetricHandler.fillPassword(pass), SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
        this.userIdField = userIdField;
        this.key = SymmetricHandler.getKey(pass, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
    }

    protected Token decipherToken(String encryptedString) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        final byte[] encryptedBack = Base64.getDecoder().decode(encryptedString.getBytes(StandardCharsets.UTF_8));
        byte[] decrypt = SymmetricHandler.decrypt(secret, encryptedBack, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
        final String decryptedString = new String(decrypt, StandardCharsets.UTF_8);
        return tokenStructure.parseAndStoreTokenDeciphered(decryptedString);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final Headers requestHeaders = exchange.getRequestHeaders();
        if (!validateHeader(requestHeaders)) {
            authRefused(exchange);
            return;
        }
        final List<String> tokenProvided = requestHeaders.get("sec");
        final List<String> userIdProvided = requestHeaders.get("id");
        String userId = userIdProvided.get(0);
        Token token = null;
        try {
            token = decipherToken(tokenProvided.get(0));
        } catch (NullPointerException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            authRefused(exchange);
        }
        if (!validateAuth(userId, token)) {
            System.err.println("token is null");
            authRefused(exchange);
        }

        handleAuthenticated(token, exchange);
    }

    private boolean validateHeader(Headers headers) {
        final List<String> tokenProvided = headers.get("sec");
        if (tokenProvided == null || tokenProvided.size() != 1) {
            return false;
        }
        final List<String> userIdProvided = headers.get("id");
        if (userIdProvided == null || userIdProvided.size() != 1) {
            return false;
        }
        return true;
    }

    private boolean validateAuth(String userId, Token token) {
        if (token == null || userId == null) {
            return false;
        }
        final String userIdFromToken = token.get(userIdField);
        return userId.equals(userIdFromToken);
    }

    private void authRefused(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(401, 0);
        exchange.getResponseBody().close();
    }

    public abstract void handleAuthenticated(Token token, HttpExchange exchange) throws IOException;

}
