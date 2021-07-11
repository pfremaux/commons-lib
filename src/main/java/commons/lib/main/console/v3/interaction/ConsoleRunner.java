package commons.lib.main.console.v3.interaction;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;
import commons.lib.main.console.v3.interaction.context.ConsoleContext;
import commons.lib.tooling.documentation.MdDoc;

@MdDoc(description = "This class initiates the interaction between the app and the user.")
public class ConsoleRunner {

    private final String contextName;
    private final ConsoleItem[] choices;

    @MdDoc(description = "Construct the console runner.")
    public ConsoleRunner(
            @MdDoc(description = "Context name. It's used to store data at the right place in AllConsoleContexts.java.") String contextName,
            @MdDoc(description = "All items the user can select.") ConsoleItem[] choices) {
        this.contextName = contextName;
        this.choices = choices;
    }

    @MdDoc(description = "Start the interaction with the user.")
    public void run() {
        ConsoleItem[] options = choices;
        while (options.length > 0) {
            AllConsoleContexts.allContexts.get(contextName).currentMenu = options;
            options = interact(options);
        }
        System.out.println("Exiting");
    }

    private ConsoleItem[] interact(ConsoleItem[] items) {
        final ConsoleContext consoleContext = AllConsoleContexts.allContexts.get(contextName);
        int response = 0;
        boolean validChoice;
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
                System.out.println("The input provided was not a number.");
            }
            validChoice = response > 0 && response <= items.length + extraChoiceQuantity;
        } while (!validChoice);
        if (response - 1 == items.length) {
            if (!consoleContext.parentMenuStack.empty()) {
                return consoleContext.parentMenuStack.pop();
            } else {
                return new ConsoleItem[0];
            }
        }
        return items[response - 1].run();
    }

}
