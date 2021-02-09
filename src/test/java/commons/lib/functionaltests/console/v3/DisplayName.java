package commons.lib.functionaltests.console.v3;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.CustomConsole;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;

public class DisplayName extends ConsoleAction {

    public DisplayName() {
        super("Display name");
    }

    @Override
    public ConsoleItem[] go() {
        CustomConsole console = ConsoleFactory.getInstance();
        console.printf("Name  = %s", ConsoleContext.cache.get("name"));
        return ConsoleContext.currentMenu;
    }
}
