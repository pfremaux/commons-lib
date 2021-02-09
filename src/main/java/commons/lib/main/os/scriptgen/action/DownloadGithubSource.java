package commons.lib.main.os.scriptgen.action;

import commons.lib.main.os.scriptgen.ScriptContext;
import commons.lib.main.os.scriptgen.ScriptPattern;

public class DownloadGithubSource extends ScriptPattern {
    public static final String LAST_GITHUB_PROJECT = "LAST_GITHUB_PROJECT";

    public DownloadGithubSource(String owner, String project) {
        super(owner, project);
    }

    @Override
    public String id() {
        return "dlGithub";
    }

    public String getCommand() {
        ScriptContext.cache.put(LAST_GITHUB_PROJECT, getParameters()[1]);
        return "github clone https://github.com/" + getParameters()[0] + "/" + getParameters()[1] + ".git" + System.lineSeparator()
                + "cd " + getParameters()[1] + System.lineSeparator();
    }
}
