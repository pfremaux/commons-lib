package commons.lib.security;

import commons.lib.filestructure.StructuredFile;
import commons.lib.security.asymetric.AsymmetricKeyHandler;
import commons.lib.security.asymetric.PrivateKeyHandler;
import commons.lib.security.asymetric.PublicKeyHandler;
import commons.lib.security.symetric.SymmetricHandler;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;

public class SimpleSecretHandler {

    private final Type type;
    private final SecretKeySpec secretKey;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private final PrivateKeyHandler privateKeyHandler;
    private PublicKeyHandler publicKeyHandler;


    public enum Type {SYMETRIC, ASYMETRIC}

    public SimpleSecretHandler(String password) {
        this.privateKeyHandler = null;
        this.publicKeyHandler = null;
        this.type = Type.SYMETRIC;
        this.secretKey = SymmetricHandler.getSecretKey(password, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
        this.publicKey = null;
        this.privateKey = null;
    }

    public SimpleSecretHandler(String publicKeyPath, String privateKeyPath) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        this.privateKeyHandler = new PrivateKeyHandler();
        this.privateKey = this.privateKeyHandler.load(privateKeyPath, AsymmetricKeyHandler.ASYMMETRIC_ALGORITHM);
        this.publicKeyHandler = new PublicKeyHandler();
        this.publicKey = this.publicKeyHandler.load(publicKeyPath, AsymmetricKeyHandler.ASYMMETRIC_ALGORITHM);
        this.type = Type.ASYMETRIC;
        this.secretKey = null;
    }

    public byte[] encrypt(String text) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        switch (type) {
            case SYMETRIC:
                return SymmetricHandler.encrypt(secretKey, text.getBytes(getCharset()), SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
            case ASYMETRIC:
                final LinkedList<PublicKey> keys = new LinkedList<>();
                keys.add(publicKey);
                final BufferedInputStream inputStream = AsymmetricKeyHandler.toBufferedInputStream(text.getBytes(Charset.forName("UTF-8")));
                final BufferedInputStream bufferedInputStream = publicKeyHandler.recursiveProcessor(keys, inputStream);
                return bufferedInputStream.readAllBytes();
            default:
                return null;
        }
    }

    private Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    public String decrypt(byte[] data) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        switch (type) {
            case SYMETRIC:
                final byte[] decrypt = SymmetricHandler.decrypt(secretKey, data, SymmetricHandler.DEFAULT_SYMMETRIC_ALGO);
                return new String(decrypt, getCharset());
            case ASYMETRIC:
                final LinkedList<PrivateKey> keys = new LinkedList<>();
                keys.add(privateKey);
                final BufferedInputStream inputStream = AsymmetricKeyHandler.toBufferedInputStream(data);
                final BufferedInputStream bufferedInputStream = privateKeyHandler.recursiveProcessor(keys, inputStream);
                return new String(bufferedInputStream.readAllBytes(), getCharset());
            default:
                return null;
        }
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        KeyPair pair = AsymmetricKeyHandler.createPair();
        PrivateKey aPrivate = pair.getPrivate();
        final PrivateKeyHandler privateKeyHandler = new PrivateKeyHandler();
        privateKeyHandler.save("./pv.key", aPrivate);
        PublicKey aPublic = pair.getPublic();
        PublicKeyHandler publicKeyHandler = new PublicKeyHandler();
        publicKeyHandler.save("./pb.key", aPublic);

        final SimpleSecretHandler simpleSecretHandler = new SimpleSecretHandler("./pb.key", "./pv.key");
        byte[] encrypt = simpleSecretHandler.encrypt("Hello there ! :)");
        System.out.println(" =>" + simpleSecretHandler.decrypt(encrypt));

        final SimpleSecretHandler simpleSecretHandler2 = new SimpleSecretHandler("./pb.key", "./pv.key");
        StructuredFile s = new StructuredFile(";");
        s.add("f");
        s.add("f");
        s.add("f");
        s.add("f");
        s.add(">");
        s.newLine();
        String text = new String(s.toByteArray(), Charset.forName("UTF-8"));
        System.out.println("Encrypting : " + text);
        byte[] encrypt1 = simpleSecretHandler2.encrypt(text);
        System.out.println(" =>" + simpleSecretHandler2.decrypt(encrypt1));
    }

}