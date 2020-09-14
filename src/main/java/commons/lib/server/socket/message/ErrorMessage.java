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

    public String getMessage() {
        return message;
    }

    public String getTechnicalMessage() {
        return technicalMessage;
    }
}
