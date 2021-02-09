package commons.lib.tooling.installer;

import commons.lib.main.SystemUtils;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.os.CommandLineExecutor;
import commons.lib.main.os.CommandStatus;
import commons.lib.main.os.scriptgen.ScriptGenerator;
import commons.lib.main.os.scriptgen.action.DeleteGitMetafile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GenerateAppInfoFileAction extends ConsoleAction {

    final String CMD_GET_CURRENT_GIT_URL = "git config --get remote.origin.url";

    public GenerateAppInfoFileAction() {
        super(String.format("Clean .git files recursively from here (%s)", Path.of(".").toFile().getAbsolutePath()));
    }

    @Override
    public ConsoleItem[] go() {

        ScriptGenerator scriptGenerator = new ScriptGenerator();
        scriptGenerator.addInstruction(new DeleteGitMetafile());
        CommandLineExecutor commandLineExecutor = new CommandLineExecutor();
        CommandStatus execute = null;
        try {
            execute = commandLineExecutor.execute(CMD_GET_CURRENT_GIT_URL);
            commandLineExecutor.waitAllCommands(1000L);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            SystemUtils.failSystem();
        }

        // git@github.com:pfremaux/project.git
        String gitUrl = execute.getLogs().toString().trim();
        int startIndex = gitUrl.lastIndexOf(':');
        int lastIndex = gitUrl.lastIndexOf('/');
        String owner = gitUrl.substring(startIndex + 1, lastIndex);
        startIndex = lastIndex;
        lastIndex = gitUrl.lastIndexOf('.');
        String projectName = gitUrl.substring(startIndex + 1, lastIndex);
        List<String> lines = new ArrayList<>();

        lines.add("app.version=0.0-dev");
        lines.add("app.name=" + projectName);
        lines.add("app.github.owner=" + owner);
        lines.add("app.github.project.name=" + projectName);
        lines.add("app.github.url.pattern=https://github.com/{0}/{1}.git");
        try {
            Files.write(Path.of("app-info.properties"), lines);
        } catch (IOException e) {
            e.printStackTrace(); // TODO LOG
        }

        return ConsoleContext.currentMenu;
    }
}
