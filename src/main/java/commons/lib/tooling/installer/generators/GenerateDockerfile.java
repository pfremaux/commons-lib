package commons.lib.tooling.installer.generators;

import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;
import commons.lib.main.os.scriptgen.ScriptPattern;
import commons.lib.tooling.installer.env.AppParametersGenerator;
import commons.lib.tooling.installer.github.GithubCallsUtils;

public class GenerateDockerfile extends ScriptPattern {

    public GenerateDockerfile(String... parameters) {
        super(parameters);
    }

    @Override
    public String id() {
        return null;
    }

    public ConsoleItem[] go() {
        StringBuilder b = new StringBuilder();
        b.append("FROM archlinux/latest");
        b.append("\n");
        ///////// ALTERNATIVELY GET BINARY FROM LOCAL REPO
        b.append("RUN git clone ");
        final String gitRepoUrl = GithubCallsUtils.buildRepoURL(getParameters()[0], getParameters()[1]);
        b.append(gitRepoUrl);
        b.append("\n");
        b.append("RUN wget "+getParameters()[2]+"/repo/dep");
        AppParametersGenerator.AppInputParameters inputParameters = AllConsoleContexts.allContexts.get("default").get(AppParametersGenerator.AppInputParameters.class);
        if (inputParameters != null) {
            AppParametersGenerator.containerInitOnBuild(b, inputParameters.getInputs());
        }

        return AllConsoleContexts.allContexts.get("default").currentMenu;
    }
}
