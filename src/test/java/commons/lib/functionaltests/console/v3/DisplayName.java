package commons.lib.functionaltests.console.v3;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.CustomConsole;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;

public class DisplayName extends ConsoleAction {

    public DisplayName() {
        super("Display name");
    }

    @Override
    public ConsoleItem[] go() {
        CustomConsole console = ConsoleFactory.getInstance();
        console.printf("Name  = %s", AllConsoleContexts.allContexts.get("default").cache.get("name"));
        return AllConsoleContexts.allContexts.get("default").currentMenu;
    }
}
