package commons.lib.extra.server.socket.secured.step4;

import commons.lib.extra.server.socket.Message;

public class AcknowledgePublicKeysMessage extends Message {

    public static final int CODE = 410;

    public AcknowledgePublicKeysMessage(String responseHostname, int responsePort, boolean requireResponse) {
        super(responseHostname, responsePort, requireResponse);
    }

    @Override
    public String[] serializeStrings() {
        return new String[]{
                getResponseHostname(),
                Integer.toString(getResponsePort()),
                Boolean.toString(isRequireResponse())
        };
    }

    @Override
    public byte[][] serializeBytes() {
        byte[][] result = new byte[3][];
        result[0] = Message.stringToBytes(getResponseHostname());
        result[1] = Message.intToBytes(getResponsePort());
        result[2] = Message.boolToBytes(isRequireResponse());
        return result;
    }
}
