package commons.lib.main.console.v3.interaction;

import commons.lib.NodeV2;

public class ActionNodeV2<T> extends ConsoleAction {

    private NodeV2<T> node;

    public ActionNodeV2(String label, NodeV2<T> node) {
        super(label);
        if (!node.isLeaf()) {
            throw new UnsupportedOperationException("A directory node can't be used as an action. Please provide a leaf instead.");
        }
        this.node = node;
    }

    @Override
    public ConsoleItem[] go() {
        System.out.println("action " + node.toString());
        return ConsoleContext.currentMenu;
    }
}
