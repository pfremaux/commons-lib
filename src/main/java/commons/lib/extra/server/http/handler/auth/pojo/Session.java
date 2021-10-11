package commons.lib.extra.server.http.handler.auth.pojo;

import commons.lib.extra.security.symetric.SymmetricHandler;
import commons.lib.main.SystemUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Session {


    public static void main(String[] args) {
        final TokenStructure tokenStructure = new TokenStructure(DefaultTokenFields.values());
        final Token token = new Token();
        token.put(DefaultTokenFields.VERSION, "1");
        token.put(DefaultTokenFields.EXPIRATION_TIMESTAMP_MS, "10000000");
        final String s = tokenStructure.getFormattedTokenInClear(token);
        System.out.println(s);
        final SecretKeySpec secret = SymmetricHandler.getKey(SymmetricHandler.fillPassword("secretsecret"), SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
        byte[] encrypt = null;
        try {
            encrypt = SymmetricHandler.encrypt(secret, s.getBytes(StandardCharsets.UTF_8), SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            SystemUtils.failSystem();
        }
        final byte[] base64Bytes = Base64.getEncoder().encode(encrypt);
        final String encryptedString = new String(base64Bytes);
        System.out.println(encryptedString);

        final byte[] encryptedBack = Base64.getDecoder().decode(encryptedString.getBytes(StandardCharsets.UTF_8));
        byte[] decrypt = null;
        try {
            decrypt = SymmetricHandler.decrypt(secret, encryptedBack, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        final String decryptedString = new String(decrypt, StandardCharsets.UTF_8);
        System.out.println("Decrypt = " + decryptedString);
        final TokenStructure tokenStructure1 = new TokenStructure(DefaultTokenFields.values());
        Token token1 = tokenStructure1.parseAndStoreTokenDeciphered(decryptedString);
        System.out.println(token1.get(DefaultTokenFields.VERSION));
        System.out.println(token1.get(DefaultTokenFields.EXPIRATION_TIMESTAMP_MS));
    }
}
