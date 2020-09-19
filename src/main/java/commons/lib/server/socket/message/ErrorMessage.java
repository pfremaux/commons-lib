package commons.lib.server.socket.message;

import commons.lib.server.socket.Message;

public class ErrorMessage extends Message {

    public static final int CODE = 9999;
    private final String message;
    private final String technicalMessage;

    public ErrorMessage(String message, String technicalMessage, String responseHostname, int responsePort, boolean requireResponse) {
        super(responseHostname, responsePort, requireResponse);
        this.message = message;
        this.technicalMessage = technicalMessage;
    }

    @Override
    public String[] serializeStrings() {
        return new String[] {
                message,
                technicalMessage,
                getResponseHostname(),
                Integer.toString(getResponsePort()),
                Boolean.toString(isRequireResponse())
        };
    }

    @Override
    public byte[][] serializeBytes() {
        byte[][] result = new byte[5][];
        result[0] = Message.stringToBytes(message);
        result[1] = Message.stringToBytes(technicalMessage);
        result[2] = Message.stringToBytes(getResponseHostname());
        result[3] = Message.intToBytes(getResponsePort());
        result[4] = Message.boolToBytes(isRequireResponse());
        return result;
    }

    public String getMessage() {
        return message;
    }

    public String getTechnicalMessage() {
        return technicalMessage;
    }
}
