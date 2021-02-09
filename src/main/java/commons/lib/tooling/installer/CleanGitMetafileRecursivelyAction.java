package commons.lib.tooling.installer;

import commons.lib.main.SystemUtils;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.os.CommandLineExecutor;
import commons.lib.main.os.CommandStatus;
import commons.lib.main.os.scriptgen.action.DeleteGitMetafile;
import commons.lib.main.os.scriptgen.ScriptGenerator;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class CleanGitMetafileRecursivelyAction extends ConsoleAction {

    public CleanGitMetafileRecursivelyAction() {
        super(String.format("Clean .git files recursively from here (%s)", Path.of(".").toFile().getAbsolutePath()));
    }

    @Override
    public ConsoleItem[] go() {
        ScriptGenerator scriptGenerator = new ScriptGenerator();
        scriptGenerator.addInstruction(new DeleteGitMetafile());
        CommandLineExecutor commandLineExecutor = new CommandLineExecutor();
        CommandStatus execute = null;
        try {
            execute = commandLineExecutor.execute(scriptGenerator.getSourceScript());
            commandLineExecutor.waitAllCommands(1000L);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            SystemUtils.failSystem();
        }

        System.out.println(execute.getLogs().toString());
        return ConsoleContext.currentMenu;
    }
}
