package commons.lib.server.socket;

import java.nio.charset.StandardCharsets;

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
        String[] strings = datum.serializeStrings();
        String[] allData = new String[strings.length + 1];
        String[] datumStrings = getDatum().serializeStrings();
        for (int i = 0; i < datumStrings.length; i++) {
            allData[i + 1] = datumStrings[i];
        }
        allData[0] = Integer.toString(action);
        String data = String.join(";", allData);
        return data.getBytes(StandardCharsets.UTF_8);
    }
}
