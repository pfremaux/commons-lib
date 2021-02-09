package commons.lib.functionaltests.console.v3;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;

public class FakeAction extends ConsoleAction {

    public FakeAction(String label) {
        super(label);
    }

    @Override
    public ConsoleItem[] go() {
        ConsoleFactory.getInstance().printf("Fake action %s done !", label());
        return ConsoleContext.currentMenu;
    }
}
