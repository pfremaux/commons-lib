package commons.lib.main.console.v3.interaction;

import commons.lib.main.os.LogUtils;
import commons.lib.tooling.documentation.MdDoc;

import java.util.logging.Logger;

@MdDoc(description = "Extend this class if you want to add an action to your menu.")
public abstract class ConsoleAction implements ConsoleItem {
    protected Logger logger = LogUtils.initLogs();
    private final String label;

    @MdDoc(description = "label is the text displayed to the user.")
    public ConsoleAction(String label) {
        this.label = label;
    }

    @Override
    public final String label() {
        return label;
    }

    @Override
    public final ConsoleItem[] run() {
        return go();
    }

    /**
     *
     * @return All allowed actions once the action hsa been done.
     */
    public abstract ConsoleItem[] go();

}
