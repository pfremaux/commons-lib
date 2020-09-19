package commons.lib.server.socket.secured.step1;

import commons.lib.server.socket.Message;

import java.nio.charset.StandardCharsets;
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

  /*  public GetServerPublicKeysMessage(List<String> serialized) {
        super(serialized.get(1), Integer.parseInt(serialized.get(2)), Boolean.parseBoolean(serialized.get(3)));
        this.symKey = serialized.get(4);
        this.nbrPublicKeyRequested = Integer.parseInt(serialized.get(5));
    }*/

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

    @Override
    public byte[][] serializeBytes() {
        byte[][] result = new byte[5][];
        result[0] = getResponseHostname().getBytes(StandardCharsets.UTF_8);
        result[1] = intToBytes(getResponsePort());
        result[2] = boolToBytes(isRequireResponse());
        result[3] = symKey.getBytes(StandardCharsets.UTF_8); // TODO migrate to bytes
        result[4] = intToBytes(nbrPublicKeyRequested);
        return result;
    }

    public String getSymKey() {
        return symKey;
    }

    public int getNbrPublicKeyRequested() {
        return nbrPublicKeyRequested;
    }
}
