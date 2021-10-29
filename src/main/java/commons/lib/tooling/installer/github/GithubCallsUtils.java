package commons.lib.tooling.installer.github;

public class GithubCallsUtils {

    public static String buildRepoURL(String owner, String projectName) {
        return "https://github.com/" + owner + "/" + projectName + ".git";
    }
}
