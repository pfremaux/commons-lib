package commons.lib.server.socket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class Message {

    private final String responseHostname;
    private final int responsePort;
    private boolean requireResponse;

    public Message(String responseHostname, int responsePort, boolean requireResponse) {
        this.responseHostname = responseHostname;
        this.responsePort = responsePort;
        this.requireResponse = requireResponse;
    }

    public static byte[] stringToBytes(String responseHostname) {
        return responseHostname.getBytes(StandardCharsets.UTF_8);
    }

    public abstract String[] serializeStrings();

    public abstract byte[][] serializeBytes();

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

    public static int bytesToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
        // return Integer.parseInt(bytesToString(bytes));
    }

    public static String bytesToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    public static byte[] intToBytes(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    public static byte[] boolToBytes(boolean value) {
        return new byte[]{value ? Byte.MAX_VALUE : Byte.MIN_VALUE};
    }

    public static boolean bytesToBool(byte[] bytes) {
        return bytes[0] == Byte.MAX_VALUE;
    }
}
