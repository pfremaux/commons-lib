package commons.lib.main.os.scriptgen.action;

import commons.lib.main.os.scriptgen.ScriptPattern;

public class DeleteGitMetafile extends ScriptPattern {

    @Override
    public String id() {
        return "installSource";
    }

    public boolean osSpecific() {
        return true;
    }

    @Override
    public String getWindowsCommand() {
        return "del /s /q *.git/\r\n" +
                "rmdir /s /q *.git/\r\n";
    }

    @Override
    public String getLinuxCommand() {
        return "rm -r -y .git/*\n" +
                "rmdir -y .git/\n";
    }
}
