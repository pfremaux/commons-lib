package commons.lib.main.console.interaction;

import java.io.Console;

public interface Choosable {
    /**
     * Typically initialize all the options if the element is chosen.
     * super.init(...)
     */
    void init();

    /**
     * @return The label as it should be displayed
     */
    String label();

    void trigger(Console console);
}
