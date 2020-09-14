package commons.lib.server.socket.secured.step4;

import commons.lib.server.socket.Message;

import java.util.List;

public class AcknowledgePublicKeysMessage extends Message {

    public static final int CODE = 410;

    public AcknowledgePublicKeysMessage(String responseHostname, int responsePort, boolean requireResponse) {
        super(responseHostname, responsePort, requireResponse);
    }

    public AcknowledgePublicKeysMessage(List<String> strings) {
        super(strings.get(0), Integer.parseInt(strings.get(1)), Boolean.parseBoolean(strings.get(2)));
    }

    @Override
    public String[] serializeStrings() {
        return new String[] {
                getResponseHostname(),
                Integer.toString(getResponsePort()),
                Boolean.toString(isRequireResponse())
        };
    }
}
