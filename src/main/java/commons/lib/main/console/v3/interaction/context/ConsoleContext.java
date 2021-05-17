package commons.lib.main.console.v3.interaction.context;

import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.tooling.documentation.MdDoc;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ConsoleContext {
    @MdDoc(description = "Use this cache if some of your actions are asking data to the user.")
    public Map<String, String> cache = new HashMap<>();
    public  ConsoleItem[] currentMenu = new ConsoleItem[0];
    @MdDoc(description = "Use this stack to memorize the path the user took while navigating through the sub level of your menu.")
    public  Stack<ConsoleItem[]> parentMenuStack = new Stack<>();
    public  Object workingObject;

    private Map<String, Object> cacheObjects = new HashMap<>();

    public <T> T get(Class<T> classz) {
        return (T) cacheObjects.get(classz.getSimpleName());
    }

    public <T> void put(T value) {
        cacheObjects.put(value.getClass().getSimpleName(), value);
    }

}
