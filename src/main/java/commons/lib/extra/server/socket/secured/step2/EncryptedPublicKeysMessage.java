package commons.lib.extra.server.socket.secured.step2;

import commons.lib.extra.server.socket.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EncryptedPublicKeysMessage extends Message {

    public static final int CODE = 401;
    private final Boolean symmetric;
    // TODO USELESS ?
    private final Boolean knowRecipientData;
    private final List<byte[]> encryptedPublicKeys;
    private final List<Integer> sizedEncryptedPublicKeys;

    public EncryptedPublicKeysMessage(List<Integer> sizedEncryptedPublicKeys, List<byte[]> encryptedPublicKeys, Boolean symmetric, Boolean knowRecipientData, String responseHostname, int responsePort, boolean requireResponse) {
        super(responseHostname, responsePort, requireResponse);
        this.sizedEncryptedPublicKeys = sizedEncryptedPublicKeys;
        this.encryptedPublicKeys = encryptedPublicKeys;
        this.symmetric = symmetric;
        this.knowRecipientData = knowRecipientData;
    }

    public EncryptedPublicKeysMessage(List<Integer> sizedEncryptedPublicKeys, byte[] concatEncryptedPublicKeys, Boolean symmetric, Boolean knowRecipientData, String responseHostname, int responsePort, boolean requireResponse) {
        super(responseHostname, responsePort, requireResponse);
        this.sizedEncryptedPublicKeys = sizedEncryptedPublicKeys;
        int encryptedDataIndex = 0;
        this.encryptedPublicKeys = new ArrayList<>();
        for (int i = 0; i < sizedEncryptedPublicKeys.size(); i++) {
            byte[] onePublicKey = new byte[sizedEncryptedPublicKeys.get(i)];
            System.arraycopy(concatEncryptedPublicKeys, encryptedDataIndex, onePublicKey, 0, sizedEncryptedPublicKeys.get(i));
            encryptedDataIndex += sizedEncryptedPublicKeys.get(i);
            encryptedPublicKeys.add(onePublicKey);
        }
        this.symmetric = symmetric;
        this.knowRecipientData = knowRecipientData;
    }

    /**
     * FIXME
     * This constructor expects the action code as a first parameter. It is not advised to implements this constructor as it is not natural to serialize without the
     * action code and deserialize with it.
     *
     * @param strings
     */
    public EncryptedPublicKeysMessage(List<String> strings) {
        super(strings.get(1), Integer.parseInt(strings.get(2)), Boolean.parseBoolean(strings.get(3)));
        this.sizedEncryptedPublicKeys = Stream.of(strings.get(6).split("-")).map(Integer::parseInt).collect(Collectors.toList());
        this.symmetric = Boolean.parseBoolean(strings.get(4));
        this.knowRecipientData = Boolean.parseBoolean(strings.get(5));
        final List<byte[]> encryptedPublicKeys = new ArrayList<>();
        int sizeCounter = 0;
        final String encryptedData = strings.get(7);
        byte[] encryptedDataBytes = encryptedData.getBytes(StandardCharsets.UTF_16);
        int encryptedDataIndex = 0;
        for (int i = 0; i < sizedEncryptedPublicKeys.size(); i++) {
            byte[] onePublicKey = new byte[sizedEncryptedPublicKeys.get(i)];
            System.arraycopy(encryptedDataBytes, encryptedDataIndex, onePublicKey, 0, sizedEncryptedPublicKeys.get(i));
            encryptedDataIndex += sizedEncryptedPublicKeys.get(i);
            encryptedPublicKeys.add(onePublicKey);
        }

        this.encryptedPublicKeys = encryptedPublicKeys;
    }

    @Override
    public String[] serializeStrings() {
        final String[] serialized = new String[7];
        serialized[0] = getResponseHostname();
        serialized[1] = Integer.toString(getResponsePort());
        serialized[2] = Boolean.toString(isRequireResponse());
        serialized[3] = Boolean.toString(isSymmetric());
        serialized[4] = Boolean.toString(isKnowRecipientData());
        final StringBuilder builder = new StringBuilder();
        sizedEncryptedPublicKeys.clear();
        for (byte[] encryptedPublicKey : encryptedPublicKeys) {
            final String str = new String(encryptedPublicKey, StandardCharsets.UTF_16);
            builder.append(str);
            sizedEncryptedPublicKeys.add(encryptedPublicKey.length);
        }
        serialized[5] = sizedEncryptedPublicKeys.stream().map(Object::toString).collect(Collectors.joining("-"));
        serialized[6] = builder.toString();
        return serialized;
    }

    @Override
    public byte[][] serializeBytes() {
        final byte[][] serialized = new byte[7][];
        serialized[0] = getResponseHostname().getBytes(StandardCharsets.UTF_8);
        serialized[1] = intToBytes(getResponsePort());
        serialized[2] = boolToBytes(isRequireResponse());
        serialized[3] = boolToBytes(isSymmetric());
        serialized[4] = boolToBytes(isKnowRecipientData());
        sizedEncryptedPublicKeys.clear();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (byte[] encryptedPublicKey : encryptedPublicKeys) {
            try {
                outputStream.write(encryptedPublicKey);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sizedEncryptedPublicKeys.add(encryptedPublicKey.length);
        }
        serialized[5] = sizedEncryptedPublicKeys.stream().map(Object::toString).collect(Collectors.joining("-")).getBytes(StandardCharsets.UTF_8);
        serialized[6] = outputStream.toByteArray();
        return serialized;
    }

    public List<byte[]> getEncryptedPublicKeys() {
        return encryptedPublicKeys;
    }

    public Boolean isKnowRecipientData() {
        return knowRecipientData;
    }

    public Boolean isSymmetric() {
        return symmetric;
    }
}
