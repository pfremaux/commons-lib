package commons.lib.main.console.v3.interaction.context;

import commons.lib.tooling.documentation.MdDoc;

import java.util.HashMap;
import java.util.Map;

@MdDoc(description = "This class provides an easy way for single threaded console apps to store common values." +
        " A context can be for example : you select an action in a menu and this action opens another menu." +
        " You want to keep some information from the parent menu and the current one.")
public class AllConsoleContexts {
    @MdDoc(description = ".")
    public static final Map<String, ConsoleContext> allContexts = new HashMap<>();

    private AllConsoleContexts() {

    }

    @MdDoc(description = "Initialize a context. It is mandatory to call it first before you store anything from a context.")
    public static void initContext(String contextKey) {
        allContexts.put(contextKey, new ConsoleContext());
    }

}
