package commons.lib.main.console.v3.interaction;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;

public class ConsoleRunner {

    private final ConsoleItem[] mainMenu;

    public ConsoleRunner(ConsoleItem[] mainMenu) {
        this.mainMenu = mainMenu;
    }

    public void run() {
        ConsoleItem[] options = mainMenu;
        while (options.length > 0) {
            ConsoleContext.currentMenu = options;
            options = interact(options);
        }
        System.out.println("Exiting");
    }

    private ConsoleItem[] interact(ConsoleItem[] items) {
        int response = 0;
        boolean validChoice;
        do {
            int i = 1;
            for (ConsoleItem item : items) {
                ConsoleFactory.getInstance().printf("%d. %s", i, item.label());
                i++;
            }
            if (!ConsoleContext.parentMenuStack.empty()) {
                ConsoleFactory.getInstance().printf("%d. %s", i, "Go back");
            } else {
                ConsoleFactory.getInstance().printf("%d. %s", i, "Exit");
            }
            String strResponse = ConsoleFactory.getInstance().readLine();
            try {
                response = Integer.parseInt(strResponse);
            } catch (NumberFormatException e) {

            }
            validChoice = response  > 0 && response <= items.length + 1; // '<=' because we added an item at the end
        } while (!validChoice);
        if (response - 1 == items.length) {
            if (!ConsoleContext.parentMenuStack.empty()) {
                return ConsoleContext.parentMenuStack.pop();
            } else {
                return new ConsoleItem[0];
            }
        }
        return items[response-1].run();
    }

}
