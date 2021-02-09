package commons.lib.functionaltests.console.v3;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.CustomConsole;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;

public class AskName extends ConsoleAction {

    public AskName() {
        super("Set name");
    }

    @Override
    public ConsoleItem[] go() {
        CustomConsole console = ConsoleFactory.getInstance();
        console.printf("Name ?");
        final String name = console.readLine();
        ConsoleContext.cache.put("name", name);
        return ConsoleContext.currentMenu;
    }
}
