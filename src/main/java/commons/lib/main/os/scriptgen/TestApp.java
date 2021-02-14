package commons.lib.main.os.scriptgen;

import commons.lib.main.os.scriptgen.action.DownloadGithubSource;
import commons.lib.main.os.scriptgen.action.InstallBinary;

public class TestApp {


    public static void main(String[] args) {
        ScriptGenerator scriptGenerator = new ScriptGenerator();
        scriptGenerator.addInstruction(new DownloadGithubSource("pfremaux", "commons-lib"));
        scriptGenerator.addInstruction(new InstallBinary());
    }
}
