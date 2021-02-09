package commons.lib.main.console;

import java.util.List;

public interface CustomConsole {
    default boolean isDebugMode() {
        return Boolean.parseBoolean(System.getProperty("mode.debug"));
    }

    void printf(String s);

    String readLine();

    List<String> history();

    char[] readPassword();

    void printf(String s, Object... objs);

    List<String> getOutputWhileDebugging();
}
