package commons.lib.tooling.installer;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleRunner;
import commons.lib.main.os.LogUtils;
import commons.lib.tooling.installer.generators.GeneratorsNavigation;
import commons.lib.tooling.installer.gitprojects.InstallGithubRepoNavigate;

import java.util.logging.Logger;

public class Launcher {
    private static Logger logger;

    public static void main(String[] args) {
        logger = LogUtils.initLogs();
        ConsoleFactory.getInstance();
        ConsoleRunner consoleRunner = new ConsoleRunner(new ConsoleItem[]{
                new InstallGithubRepoNavigate(),
                new GeneratorsNavigation()
        });
        consoleRunner.run();
    }
}
