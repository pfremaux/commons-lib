package commons.lib.tooling.installer.gitprojects;

import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleNavigation;

public class InstallGithubRepoNavigate extends ConsoleNavigation {
    private final ConsoleItem[] menu  = new ConsoleItem[] {
            new InstallMaquetteAction(),
            new InstallPasswordManagerAction()
    };
    public InstallGithubRepoNavigate() {
        super("default", "Install a github project.");
    }

    @Override
    public ConsoleItem[] navigate() {
        return menu;
    }
}
