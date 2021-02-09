package commons.lib.main.os.scriptgen.action;

import commons.lib.main.os.scriptgen.ScriptPattern;

public class CopyFile extends ScriptPattern {

    @Override
    public String id() {
        return "CopyFile";
    }

    @Override
    public boolean osSpecific() {
        return true;
    }

    @Override
    public String getWindowsCommand() {
        return "copy " + getParameters()[0] + " "+ getParameters()[1];
    }

    @Override
    public String getLinuxCommand() {
        return "cp " + getParameters()[0] + " "+ getParameters()[1];
    }
}
