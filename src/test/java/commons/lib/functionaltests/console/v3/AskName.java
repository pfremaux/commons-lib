package commons.lib.functionaltests.console.v3;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.CustomConsole;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;

public class AskName extends ConsoleAction {

    public static final String DEFAULT = "default";

    public AskName() {
        super("Set name");
    }

    @Override
    public ConsoleItem[] go() {
        CustomConsole console = ConsoleFactory.getInstance();
        console.printf("Name ?");
        final String name = console.readLine();
        // AllConsoleContexts.allContexts.put(DEFAULT, new ConsoleContext());
        AllConsoleContexts.allContexts.get(DEFAULT).cache.put("name", name);
        return AllConsoleContexts.allContexts.get(DEFAULT).currentMenu;
    }
}
