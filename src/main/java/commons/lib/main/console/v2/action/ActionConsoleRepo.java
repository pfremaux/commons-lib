package commons.lib.main.console.v2.action;

import commons.lib.main.console.v2.Choice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ActionConsoleRepo {

    private static Map<Choice, Function<String, ActionConsole>> all = new HashMap<>();
    private static boolean available;
    public static ActionConsole root = null;

    public static void register(Choice choice, Function<String, ActionConsole> c) throws IllegalAccessException {
        if (available) {
            throw new IllegalAccessException("Parameters locked. Can't add more");
        }
        all.put(choice, c);
    }

    public static ActionConsole get(Choice c) {
        if (!available) {
            throw new RuntimeException("You have to lock InputParametersRepo first.");
        }
        return all.get(c).apply("");
    }

    public static Map<Choice, Function<String, ActionConsole>> getAll() {
        if (available) {
            return all;
        }
        throw new RuntimeException("You have to lock InputParametersRepo first.");
    }

    public static void lock() {
        all = Collections.unmodifiableMap(all);
        available = true;
    }
}
