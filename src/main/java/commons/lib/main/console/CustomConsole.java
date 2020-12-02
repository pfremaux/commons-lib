package commons.lib.main.console;

import java.util.List;

public interface CustomConsole {
    void printf(String s);
    String readLine();
    List<String> history();

    char[] readPassword();

    void printf(String s, Object... objs);
}
