package commons.lib.main.console.v3.interaction;

public class ConsoleNavigateParent extends ConsoleNavigation {

    private ConsoleItem[] parentMenu;

    public ConsoleNavigateParent(ConsoleItem[] parentMenu) {
        super("Go back");
        this.parentMenu = parentMenu;
    }

    @Override
    public ConsoleItem[] navigate() {
        return parentMenu;
    }

    @Override
    public int ordering() {
        return super.ordering();
    }
}
