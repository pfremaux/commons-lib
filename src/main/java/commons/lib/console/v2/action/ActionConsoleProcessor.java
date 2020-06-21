package commons.lib.console.v2.action;

import commons.lib.console.v2.Choice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ActionConsoleProcessor {
    private static Map<Choice, Consumer<ActionConsole>> all = new HashMap<>();
    private static boolean available;

    public static void process(ActionConsole actionConsole) {
        if (!available) {
            throw new RuntimeException("You have to lock ActionConsoleProcessor first.");
        }
        Choice context = actionConsole.getContext();
        Consumer<ActionConsole> actionConsoleConsumer = all.get(context);
        if (actionConsoleConsumer != null) {
            actionConsoleConsumer.accept(actionConsole);
        }
    }

    public static void register(Choice c, Consumer<ActionConsole> a) throws IllegalAccessException {
        if (available) {
            throw new IllegalAccessException("Parameters locked. Can't add more");
        }
        all.put(c, a);
    }

    public static void lock() {
        all = Collections.unmodifiableMap(all);
        available = true;
    }
}
