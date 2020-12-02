package commons.lib.main.console.v2;

import commons.lib.main.console.AutomateConsole;
import commons.lib.main.console.CustomConsole;

import java.nio.file.Path;

public class Console {
    private static CustomConsole instance = null;

    public static CustomConsole get() {
        return instance;
    }

    public static CustomConsole get(Path inputFile) {
        instance = new AutomateConsole(inputFile);
        return instance;
    }
}
