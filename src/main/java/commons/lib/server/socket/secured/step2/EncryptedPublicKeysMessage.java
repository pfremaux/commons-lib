package commons.lib.server.socket.secured.step2;

import commons.lib.server.socket.Message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EncryptedPublicKeysMessage extends Message {

    public static final int CODE = 401;
    private final Boolean symmetric;
    // TODO USELESS ?
    private final Boolean knowRecipientData;
    private final List<byte[]> encryptedPublicKeys;

    public EncryptedPublicKeysMessage(List<byte[]> encryptedPublicKeys, Boolean symmetric, Boolean knowRecipientData, String responseHostname, int responsePort, boolean requireResponse) {
        super(responseHostname, responsePort, requireResponse);
        this.encryptedPublicKeys = encryptedPublicKeys;
        this.symmetric = symmetric;
        this.knowRecipientData = knowRecipientData;
    }

    public EncryptedPublicKeysMessage(List<String> strings) {
        super(strings.get(0), Integer.parseInt(strings.get(1)), Boolean.parseBoolean(strings.get(2)));
        this.symmetric = Boolean.parseBoolean(strings.get(3));
        this.knowRecipientData = Boolean.parseBoolean(strings.get(4));
        final List<byte[]> encryptedPublicKeys = new ArrayList<>();
        for (int i = 5; i < strings.size(); i++) {
            encryptedPublicKeys.add(strings.get(i).getBytes());
        }
        this.encryptedPublicKeys = encryptedPublicKeys;
    }

    @Override
    public String[] serializeStrings() {
        final String[] serialized = new String[encryptedPublicKeys.size() + 3];
        serialized[0] = getResponseHostname();
        serialized[1] = Integer.toString(getResponsePort());
        serialized[2] = Boolean.toString(isRequireResponse());
        for (int i = 3; i < encryptedPublicKeys.size() + 3; i++) {
            serialized[i] = new String(encryptedPublicKeys.get(i - 3), StandardCharsets.UTF_8);
        }
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
