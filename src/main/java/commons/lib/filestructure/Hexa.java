package commons.lib.filestructure;

import org.slf4j.Logger;

public class Hexa {
    public static void display(byte b) {
        System.out.printf("%02X", b);
    }


    public static void display(byte[] bytes, int lineLength) {
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append(String.format("%02X", aByte));
            builder.append(" ");
            if (builder.length() >= lineLength) {
                System.out.println(builder.toString());
                builder = new StringBuilder();
            }
        }
        if (builder.length() > 0) {
            System.out.println(builder.toString());
        }
    }

    public static void log(Logger logger, byte[] bytes, int lineLength) {
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append(String.format("%02X", aByte));
            builder.append(" ");
            if (builder.length() >= lineLength) {
                logger.info(builder.toString());
                builder = new StringBuilder();
            }
        }
        if (builder.length() > 0) {
            logger.info(builder.toString());
        }
    }

    public static void main(String[] args) {
        display("\n\ra".getBytes(), 16);
    }
}
