package commons.lib.tooling.installer.generators;

import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;

public class GenerateJavaFromSqlAction extends ConsoleAction {


    public GenerateJavaFromSqlAction() {
        super("Generate Java from SQL");
    }

    @Override
    public ConsoleItem[] go() {
        System.out.println("Not yet implemented.");
        return AllConsoleContexts.allContexts.get("default").currentMenu;
    }
}
