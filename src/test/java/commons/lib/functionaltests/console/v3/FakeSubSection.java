package commons.lib.functionaltests.console.v3;

import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleNavigation;

public class FakeSubSection extends ConsoleNavigation {

    private final ConsoleItem[] subMenu = new ConsoleItem[] {
            new FakeAction("fake action 1"),
            new FakeAction("fake action 2"),
            new FakeAction("fake action 3"),
            new FakeAction("fake action 4")
    };

    public FakeSubSection() {
        super("Fake sub section");
    }

    @Override
    public ConsoleItem[] navigate() {
        // ConsoleContext.currentMenu = subMenu;
        return subMenu;
    }
}
