package commons.lib.extra.server.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * This wrapper is used for managing message exchange between 2 apps.
 */
public class Wrapper {
    private final int action;
    private final Message datum;

    /**
     * @param action For each action in your app this value must be unique in order to the consumer to cast the Message in a correct way.
     * @param datum  The message that will be cast in a more specific type depending on the action value.
     */
    public Wrapper(int action, Message datum) {
        this.action = action;
        this.datum = datum;
    }

    public int getAction() {
        return action;
    }

    public Message getDatum() {
        return datum;
    }

    /**
     * Method used to convert the class to a valid format in order to send it to another app.
     *
     * @return Bytes of the current object.
     */
    public byte[] serialize() {
        // return getByteArrayOutputStreamLegacy();
        // 4 bytes for the action code : integer => 4 bytes
        int totalLengthInBytes = 4;
        byte[][] bytes = getDatum().serializeBytes();
        for (byte[] aByte : bytes) {
            // +1 for the semi colon ';'
            totalLengthInBytes += 1 + aByte.length;
        }
        byte[] wholeData = new byte[totalLengthInBytes];
        int indexData = 0;
        byte[] bytesAction = Message.intToBytes(action);
        System.arraycopy(bytesAction, 0, wholeData, indexData, bytesAction.length);
        indexData += bytesAction.length;
        for (byte[] aByte : bytes) {
            wholeData[indexData] = ';';
            indexData++;
            System.arraycopy(aByte, 0, wholeData, indexData, aByte.length);
            indexData += aByte.length;
        }
        return wholeData;
    }

    private ByteArrayOutputStream getByteArrayOutputStreamLegacy() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[][] bytes = getDatum().serializeBytes();
        try {
            outputStream.write(Message.intToBytes(action));
            for (byte[] aByte : bytes) {
                outputStream.write(';');
                outputStream.write(aByte);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream;
    }

    private byte[] intToBytes(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }
}
