package commons.lib.main.console.v3.interaction;


import commons.lib.NodeV2;

import java.util.ArrayList;
import java.util.List;

public class NavigateNodeV2<T> extends ConsoleNavigation {

    private NodeV2<T> node;

    public NavigateNodeV2(String label, NodeV2<T> node) {
        super(label);
        if (node.isLeaf()) {
            throw new UnsupportedOperationException("A leaf node can't be used as a console navigation. Please provide a directory node instead.");
        }
        this.node = node;
    }

    @Override
    public ConsoleItem[] navigate() {
        final List<ConsoleItem> consoleItems = new ArrayList<>();
        for (NodeV2<T> nodeV2 : node.getChildren()) {
            if (nodeV2.isLeaf()) {
                consoleItems.add(new ActionNodeV2<T>(nodeV2.getNodeName(), nodeV2));
            } else {
                consoleItems.add(new NavigateNodeV2<T>(nodeV2.getNodeName(), nodeV2));
            }
        }
        if (!ConsoleContext.parentMenuStack.empty()) {
            consoleItems.add(GO_BACK);
        }
        ConsoleContext.currentMenu = consoleItems.toArray(new ConsoleItem[0]);
        return ConsoleContext.currentMenu;
    }

}
