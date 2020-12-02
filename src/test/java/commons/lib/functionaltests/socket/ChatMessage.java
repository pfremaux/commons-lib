package commons.lib.functionaltests.socket;

import commons.lib.extra.server.socket.Message;

public class ChatMessage extends Message {

    private final String message;

    public ChatMessage(String message, String responseHostname, int responsePort, boolean requireResponse) {
        super(responseHostname, responsePort, requireResponse);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String[] serializeStrings() {
        return new String[]{
                getResponseHostname(),
                Integer.toString(getResponsePort()),
                Boolean.toString(isRequireResponse()),
                message
        };
    }

    @Override
    public byte[][] serializeBytes() {
        byte[][] result = new byte[4][];
        result[0] = Message.stringToBytes(getResponseHostname());
        result[1] = Message.intToBytes(getResponsePort());
        result[2] = Message.boolToBytes(isRequireResponse());
        result[3] = Message.stringToBytes(message);
        return result;
    }
}
