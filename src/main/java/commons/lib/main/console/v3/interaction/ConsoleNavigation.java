package commons.lib.main.console.v3.interaction;

import commons.lib.tooling.documentation.MdDoc;

@MdDoc(description = "Extend this class if you want to make the user navigate in a sub level.")
public abstract class ConsoleNavigation implements ConsoleItem {

    private final String label;
    private ConsoleItem[] parentMenu;

    public ConsoleNavigation(String label) {
        this.label = label;
    }

    @Override
    public final String label() {
        return label;
    }

    @Override
    public final ConsoleItem[] run() {
        ConsoleItem[] newMenu = navigate();
        /*List<ConsoleItem> consoleItems = new ArrayList<>(Arrays.asList(newMenu));
        consoleItems.add(new ConsoleNavigateParent(ConsoleContext.currentMenu));*/
        ConsoleContext.parentMenuStack.push(ConsoleContext.currentMenu);
        return newMenu;
    }

    public ConsoleItem[] getParentMenu() {
        return parentMenu;
    }

    @MdDoc(description = "Triggered when the user selects this item. You must return the items of the sub level.")
    public abstract ConsoleItem[] navigate();

    @Override
    public int ordering() {
        return 0;
    }
}
