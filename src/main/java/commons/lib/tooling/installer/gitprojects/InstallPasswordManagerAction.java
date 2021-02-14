package commons.lib.tooling.installer.gitprojects;

import commons.lib.main.SystemUtils;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.os.CommandLineExecutor;
import commons.lib.main.os.CommandStatus;
import commons.lib.main.os.scriptgen.ScriptGenerator;
import commons.lib.main.os.scriptgen.action.DownloadGithubSource;
import commons.lib.main.os.scriptgen.action.InstallBinary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class InstallPasswordManagerAction extends ConsoleAction {

    private final String GIT_OWNER = "pfremaux";

    public InstallPasswordManagerAction() {
        super("Install PasswordManager");
    }

    @Override
    public ConsoleItem[] go() {
        final ScriptGenerator scriptGenerator = new ScriptGenerator();
        scriptGenerator.addInstruction(new DownloadGithubSource(GIT_OWNER, "passwords"));
        scriptGenerator.addInstruction(new InstallBinary());
        final String sourceScript = scriptGenerator.getSourceScript();
        String installScript = "./get-passwords" + scriptGenerator.getScriptExtension();
        try {
            Files.writeString(Paths.get(installScript), sourceScript);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final CommandLineExecutor commandLineExecutor = new CommandLineExecutor();
        CommandStatus execute = null;
        try {
            execute = commandLineExecutor.execute(installScript);
            commandLineExecutor.waitAllCommands(1000L);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            SystemUtils.failSystem();
        }
        return ConsoleContext.currentMenu;
    }
}
