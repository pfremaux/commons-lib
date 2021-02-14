package commons.lib.main.console.v3.interaction;

import commons.lib.tooling.documentation.MdDoc;

@MdDoc(description = "Extend this class if you want to make the user navigate in a sub level.")
public abstract class ConsoleNavigation implements ConsoleItem {

    protected static final ConsoleNavigation GO_BACK = new ConsoleNavigation("Go back") {
        @Override
        public ConsoleItem[] navigate() {
            ConsoleContext.parentMenuStack.pop();
            return ConsoleContext.parentMenuStack.pop();
        }
    };

    private final String label;

    public ConsoleNavigation(String label) {
        this.label = label;
    }

    @Override
    public final String label() {
        return label;
    }

    @Override
    public final ConsoleItem[] run() {
        ConsoleContext.parentMenuStack.push(ConsoleContext.currentMenu);
        ConsoleItem[] newMenu = navigate();
        return newMenu;
    }


    @MdDoc(description = "Triggered when the user selects this item. You must return the items of the sub level.")
    public abstract ConsoleItem[] navigate();

    @Override
    public int ordering() {
        return 0;
    }
}
