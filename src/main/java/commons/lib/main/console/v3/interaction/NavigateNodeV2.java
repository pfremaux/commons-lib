package commons.lib.main.console.v3.interaction;


import commons.lib.NodeV2;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;
import commons.lib.main.console.v3.interaction.context.ConsoleContext;

import java.util.ArrayList;
import java.util.List;

public class NavigateNodeV2<T> extends ConsoleNavigation {

    private final String contextName;
    private NodeV2<T> node;

    public NavigateNodeV2(String contextName, String label, NodeV2<T> node) {
        super(contextName, label);
        this.contextName = contextName;
        if (node != null && node.isLeaf()) {
            throw new UnsupportedOperationException("A leaf node can't be used as a console navigation. Please provide a directory node instead.");
        }
        this.node = node;
    }

    @Override
    public ConsoleItem[] navigate() {
        final ConsoleContext consoleContext = AllConsoleContexts.allContexts.get(contextName);
        if (node == null) {
            // TODO adjust
            System.out.println("empty");
            consoleContext.currentMenu =  new ConsoleNavigation[] {};
            return consoleContext.currentMenu;
        }
        final List<ConsoleItem> consoleItems = new ArrayList<>();
        for (NodeV2<T> nodeV2 : node.getChildren()) {
            if (nodeV2.isLeaf()) {
                consoleItems.add(new ActionNodeV2<T>(contextName, nodeV2.getNodeName(), nodeV2));
            } else {
                consoleItems.add(new NavigateNodeV2<T>(contextName, nodeV2.getNodeName(), nodeV2));
            }
        }
        if (!consoleContext.parentMenuStack.empty()) {
            consoleItems.add(GO_BACK);
        }
        consoleContext.currentMenu = consoleItems.toArray(new ConsoleItem[0]);
        return consoleContext.currentMenu;
    }

}
