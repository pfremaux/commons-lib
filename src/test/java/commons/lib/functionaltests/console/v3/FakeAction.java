package commons.lib.functionaltests.console.v3;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;

public class FakeAction extends ConsoleAction {

    public FakeAction(String label) {
        super(label);
    }

    @Override
    public ConsoleItem[] go() {
        ConsoleFactory.getInstance().printf("Fake action %s done !", label());
        return AllConsoleContexts.allContexts.get("default").currentMenu;
    }
}
