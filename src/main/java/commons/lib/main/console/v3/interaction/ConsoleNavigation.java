package commons.lib.main.console.v3.interaction;

import commons.lib.main.os.LogUtils;
import commons.lib.tooling.documentation.MdDoc;

import java.util.logging.Logger;

@MdDoc(description = "Extend this class if you want to make the user navigate in a sub level.")
public abstract class ConsoleNavigation implements ConsoleItem {

    private Logger logger = LogUtils.initLogs();

    protected static final ConsoleNavigation GO_BACK = new ConsoleNavigation("Go back") {
        @Override
        public ConsoleItem[] navigate() {
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
        if (GO_BACK != this) {
            logger.info("Navigate : push to the stack");
            ConsoleContext.parentMenuStack.push(ConsoleContext.currentMenu);
        } else {
            logger.info("Going back, don't push to the stack");
        }
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
