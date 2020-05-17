package commons.lib.server.socket;

public abstract class Message {

    private final String responseHostname;
    private final int responsePort;
    private boolean requireResponse;

    public Message(String responseHostname, int responsePort, boolean requireResponse) {
        this.responseHostname = responseHostname;
        this.responsePort = responsePort;
        this.requireResponse = requireResponse;
    }

    public abstract String[] serializeStrings();

    public boolean isRequireResponse() {
        return requireResponse;
    }

    public void setRequireResponse(boolean requireResponse) {
        this.requireResponse = requireResponse;
    }

    public String getResponseHostname() {
        return responseHostname;
    }

    public int getResponsePort() {
        return responsePort;
    }
}
