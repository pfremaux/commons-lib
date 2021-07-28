package commons.lib.main.console.v3.interaction;

import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;
import commons.lib.main.console.v3.interaction.context.ConsoleContext;
import commons.lib.main.os.LogUtils;
import commons.lib.tooling.documentation.MdDoc;

import java.util.logging.Logger;

@MdDoc(description = "Extend this class if you want to make the user navigate in a sub level.")
public abstract class ConsoleNavigation implements ConsoleItem {

    private Logger logger = LogUtils.initLogs();
    // TODO nettoyer car cet attribut est aussi déclaré dans les class enfants
    protected String contextName;

    public static final ConsoleNavigation GO_BACK = new ConsoleNavigation("default", "Go back") {
        @Override
        public ConsoleItem[] navigate() {
            return AllConsoleContexts.allContexts.get(contextName).parentMenuStack.pop();
        }

    };

    private final String label;

    public ConsoleNavigation(String contextName, String label) {
        this.contextName = contextName;
        this.label = label;
    }

    @Override
    public final String label() {
        return label;
    }

    @Override
    public final ConsoleItem[] run() {
        if (GO_BACK != this) {
            LogUtils.debug("Navigate : push to the stack");
            final ConsoleContext consoleContext = AllConsoleContexts.allContexts.get(contextName);
            consoleContext.parentMenuStack.push(consoleContext.currentMenu);
        } else {
            LogUtils.debug("Going back, don't push to the stack");
        }
        ConsoleItem[] newMenu = navigate();
        return newMenu;
    }


    @MdDoc(description = "Triggered when the user selects this item. You must return the items of the sub level.")
    public abstract ConsoleItem[] navigate();

    public String getContextName() {
        return contextName;
    }

    public ConsoleNavigation withContextName(String contextName) {
        this.contextName = contextName;
        return this;
    }

    @Override
    public int ordering() {
        return 0;
    }
}
