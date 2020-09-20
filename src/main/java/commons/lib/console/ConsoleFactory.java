package commons.lib.console;

import java.nio.file.Path;
import java.util.List;

public class ConsoleFactory {

    private static CustomConsole instance = null;

    public static CustomConsole getInstance() {
        return getInstance((Path) null);
    }

    public static CustomConsole getInstance(List<String> answers) {
        if (instance == null) {
            if (answers != null) {
                instance = new AutomateConsole(answers);
            } else {
                instance = new RealConsole();
            }
        }
        return instance;
    }

    public static CustomConsole getInstance(Path inputFile) {
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
