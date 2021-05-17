package commons.lib.main.console.v3.interaction;

import commons.lib.NodeV2;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;

public class ActionNodeV2<T> extends ConsoleAction {

    private final String contextName;
    private NodeV2<T> node;

    public ActionNodeV2(String contextName, String label, NodeV2<T> node) {
        super(label);
        this.contextName = contextName;
        if (!node.isLeaf()) {
            throw new UnsupportedOperationException("A directory node can't be used as an action. Please provide a leaf instead.");
        }
        this.node = node;
    }

    @Override
    public ConsoleItem[] go() {
        System.out.println("action " + node.toString());
        return AllConsoleContexts.allContexts.get(contextName).currentMenu;
    }
}
