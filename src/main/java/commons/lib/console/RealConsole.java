package commons.lib.console;

import commons.lib.console.CustomConsole;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class RealConsole implements CustomConsole {

    private List<String> history = new ArrayList<>();
    private final Console console;

    public RealConsole() {
        this.console = System.console();
    }

    @Override
    public void show(String s) {
        console.printf(s + "\n");
    }

    @Override
    public String readLine() {
        String line = console.readLine();
        history.add(line);
        return line;
    }

    @Override
    public List<String> history() {
        return history;
    }
}
