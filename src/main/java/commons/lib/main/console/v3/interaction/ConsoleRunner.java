package commons.lib.main.console.v3.interaction;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;
import commons.lib.main.console.v3.interaction.context.ConsoleContext;

public class ConsoleRunner {

    private final String contextName;
    private final ConsoleItem[] mainMenu;

    public ConsoleRunner(String contextName, ConsoleItem[] mainMenu) {
        this.contextName = contextName;
        this.mainMenu = mainMenu;
    }

    public void run() {
        ConsoleItem[] options = mainMenu;
        while (options.length > 0) {
            AllConsoleContexts.allContexts.get(contextName).currentMenu =options;
            options = interact(options);
        }
        System.out.println("Exiting");
    }

    private ConsoleItem[] interact(ConsoleItem[] items) {
        int response = 0;
        boolean validChoice;
        ConsoleContext consoleContext = AllConsoleContexts.allContexts.get(contextName);
        do {
            int extraChoiceQuantity = 0;
            int i = 1;
            // Displays all items to the user with an index for each of them
            for (ConsoleItem item : items) {
                ConsoleFactory.getInstance().printf("%d. %s", i, item.label());
                i++;
            }
            // If we're at the root level then displays an extra option : for exiting
            if (consoleContext.parentMenuStack.empty()) {
                ConsoleFactory.getInstance().printf("%d. %s", i, "Exit");
                extraChoiceQuantity++;
            }
            final String strResponse = ConsoleFactory.getInstance().readLine();
            try {
                response = Integer.parseInt(strResponse);
            } catch (NumberFormatException e) {
                System.out.println("fail");
            }
            validChoice = response  > 0 && response <= items.length + extraChoiceQuantity;
        } while (!validChoice);
        if (response - 1 == items.length) {
            if (!consoleContext.parentMenuStack.empty()) {
                return consoleContext.parentMenuStack.pop();
            } else {
                return new ConsoleItem[0];
            }
        }
        return items[response-1].run();
    }

}
