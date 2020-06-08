package commons.lib.console;

import java.util.List;

public interface CustomConsole {
    void show(String s);
    String readLine();
    List<String> history();
}
