package commons.lib.server.socket.secured.step1;

import commons.lib.server.socket.Message;

import java.util.List;

public class GetServerPublicKeysMessage extends Message {

    public static final int CODE = 400;
    private final String symKey;
    private final int nbrPublicKeyRequested;

    public GetServerPublicKeysMessage(String symKey, int nbrPublicKeyRequested, String responseHostname, int responsePort, boolean requireResponse) {
        super(responseHostname, responsePort, requireResponse);
        this.symKey = symKey;
        this.nbrPublicKeyRequested = nbrPublicKeyRequested;
    }

    public GetServerPublicKeysMessage(List<String> serialized) {
        super(serialized.get(0), Integer.parseInt(serialized.get(1)), Boolean.parseBoolean(serialized.get(2)));
        this.symKey = serialized.get(3);
        this.nbrPublicKeyRequested = Integer.parseInt(serialized.get(4));
    }

    @Override
    public String[] serializeStrings() {
        return new String[]{
                getResponseHostname(),
                Integer.toString(getResponsePort()),
                Boolean.toString(isRequireResponse()),
                symKey,
                Integer.toString(nbrPublicKeyRequested)
        };
    }

    public String getSymKey() {
        return symKey;
    }

    public int getNbrPublicKeyRequested() {
        return nbrPublicKeyRequested;
    }
}
