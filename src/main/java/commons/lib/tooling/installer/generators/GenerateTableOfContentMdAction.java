package commons.lib.tooling.installer.generators;

import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.os.LogUtils;
import commons.lib.tooling.documentation.Documentation;
import commons.lib.tooling.markdown.MarkdownGenerator;
import commons.lib.tooling.markdown.TocGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class GenerateTableOfContentMdAction extends ConsoleAction {

    private static final Logger logger = LogUtils.initLogs();

    public GenerateTableOfContentMdAction() {
        super("Generate generate MarkDown documentation");
    }

    @Override
    public ConsoleItem[] go() {
        final Path baseDocumentationDir = Path.of(System.getProperty("user.dir"));
        try {
            final TocGenerator tocGenerator = new TocGenerator(baseDocumentationDir);
            Documentation toc = tocGenerator.run();
            MarkdownGenerator markdownGenerator = new MarkdownGenerator(baseDocumentationDir);
            markdownGenerator.generate(toc, "tableOfContent.md");
        } catch (IOException e) {
            logger.throwing(this.getClass().getSimpleName(), "go()", e);
        }
        return ConsoleContext.currentMenu;
    }
}
