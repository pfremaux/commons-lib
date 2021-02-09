package commons.lib.tooling.installer;

import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleRunner;
import commons.lib.main.os.LogUtils;

import java.util.logging.Logger;

public class Launcher {
    private static Logger logger;
    public static void main(String[] args) {
       // System.setProperty("isInIDE", "true");
        logger = LogUtils.initLogs();
        ConsoleRunner consoleRunner = new ConsoleRunner(new ConsoleItem[]{
                new InstallGithubRepoNavigate()
        });
        consoleRunner.run();
    }
}
