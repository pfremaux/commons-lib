package commons.lib.tooling.installer.generators;

import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleNavigation;

public class GeneratorsNavigation extends ConsoleNavigation {

    public static final ConsoleItem[] CONSOLE_ITEMS = {
            new GenerateJavaFromSqlAction(),
            new GenerateAppInfoFileAction(),
            new GenerateMdDocumentationAction(),
            new GenerateTableOfContentMdAction()
    };

    public GeneratorsNavigation() {
        super("default", "Generators...");
    }

    @Override
    public ConsoleItem[] navigate() {
        return CONSOLE_ITEMS;
    }
}
