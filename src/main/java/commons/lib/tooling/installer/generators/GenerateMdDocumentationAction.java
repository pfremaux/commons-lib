package commons.lib.tooling.installer.generators;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;
import commons.lib.main.os.LogUtils;
import commons.lib.tooling.markdown.GenerateMd;

import java.io.IOException;
import java.util.logging.Logger;

public class GenerateMdDocumentationAction extends ConsoleAction {

    private static final Logger logger = LogUtils.initLogs();

    public GenerateMdDocumentationAction() {
        super("Generate generate MarkDown documentation");
    }

    @Override
    public ConsoleItem[] go() {
        ConsoleFactory.getInstance().printf("package name ?");
        final String packageName = ConsoleFactory.getInstance().readLine();
        final GenerateMd generateMd = new GenerateMd(packageName);
        try {
            generateMd.run();
        } catch (IOException | ClassNotFoundException e) {
            logger.throwing(this.getClass().getSimpleName(), "go()", e);
        }
        return AllConsoleContexts.allContexts.get("default").currentMenu;
    }
}
