package commons.lib.main.console;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class RealConsole implements CustomConsole {

    private List<String> history = new ArrayList<>();
    private List<String> outputWhileDebugging = new ArrayList<>();
    private final Console console;

    public RealConsole() {
        this.console = System.console();
    }

    public RealConsole(Console console) {
        this.console = console;
    }

    @Override
    public void printf(String s) {
        if (isDebugMode()) {
            outputWhileDebugging.add(s);
        }
        console.printf(s + "\n");
    }

    @Override
    public String readLine() {
        String line = console.readLine();
        history.add(line);
        if (isDebugMode()) {
            outputWhileDebugging.add(line);
        }
        return line;
    }

    @Override
    public List<String> history() {
        return history;
    }

    @Override
    public char[] readPassword() {
        return console.readPassword();
    }

    @Override
    public void printf(String s, Object... objs) {
        if (isDebugMode()) {
            outputWhileDebugging.add(String.format(s, objs));
        }
        console.printf(s + "\n", objs);
    }

    public List<String> getOutputWhileDebugging() {
        return outputWhileDebugging;
    }
}
