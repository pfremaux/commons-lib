package commons.lib.main.console.v3.interaction;

import commons.lib.tooling.documentation.MdDoc;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@MdDoc(description = "This class contains exclusively static caches.")
public class ConsoleContext {
    @MdDoc(description = "Use this cache if some of your actions are asking data to the user.")
    public static Map<String, String> cache = new HashMap<>();
    public static ConsoleItem[] currentMenu = new ConsoleItem[0];
    @MdDoc(description = "Use this stack to memorize the path the user took while navigating through the sub level of your menu.")
    public static Stack<ConsoleItem[]> parentMenuStack = new Stack<>();

    private ConsoleContext() {}
}
