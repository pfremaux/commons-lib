package commons.lib.console;

import java.nio.file.Path;

public class ConsoleFactory {

    private static CustomConsole instance = null;

    public static CustomConsole getInstance(boolean fake) {
        return getInstance(fake, null);
    }

    public static CustomConsole getInstance(boolean fake, Path inputFile) {
        if (instance == null) {
            if (inputFile != null && inputFile.toFile().exists()) {
                instance = new AutomateConsole(inputFile);
            } else {
                instance = new RealConsole();
            }
        }
        return instance;
    }
}
