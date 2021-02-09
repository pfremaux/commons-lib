package commons.lib.tooling.installer;

import commons.lib.main.SystemUtils;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.os.CommandLineExecutor;
import commons.lib.main.os.CommandStatus;
import commons.lib.main.os.scriptgen.ScriptGenerator;
import commons.lib.main.os.scriptgen.action.DownloadGithubSource;
import commons.lib.main.os.scriptgen.action.InstallSource;

import java.util.concurrent.ExecutionException;

public class InstallMaquetteAction extends ConsoleAction {

    private final String GIT_OWNER = "pfremaux";

    public InstallMaquetteAction() {
        super("Install Maquette");
    }

    @Override
    public ConsoleItem[] go() {
        ScriptGenerator scriptGenerator = new ScriptGenerator();
        scriptGenerator.addInstruction(new DownloadGithubSource(GIT_OWNER, "maquette"));
        scriptGenerator.addInstruction(new InstallSource());
        CommandLineExecutor commandLineExecutor = new CommandLineExecutor();
        CommandStatus execute = null;
        try {
            execute = commandLineExecutor.execute("git clone http://github.com/" + GIT_OWNER + "/");
            commandLineExecutor.waitAllCommands(1000L);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            SystemUtils.failSystem();
        }

        System.out.println(execute.getLogs().toString());
        return ConsoleContext.currentMenu;
    }
}
