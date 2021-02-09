package commons.lib.tooling.installer;

import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleNavigation;

public class InstallGithubRepoNavigate extends ConsoleNavigation {
    private final ConsoleItem[] menu  = new ConsoleItem[] {
            new InstallMaquetteAction()
    };
    public InstallGithubRepoNavigate() {
        super("Install a github project.");
    }

    @Override
    public ConsoleItem[] navigate() {
        return menu;
    }
}
