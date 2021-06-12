package commons.lib.main.console.v3.interaction.context;

import java.util.HashMap;
import java.util.Map;

public class AllConsoleContexts {
    public static final Map<String, ConsoleContext> allContexts = new HashMap<>();
    private  AllConsoleContexts() {

    }

    public static void initContext(String key) {
        allContexts.put(key, new ConsoleContext());
    }

}
