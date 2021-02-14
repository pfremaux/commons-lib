package commons.lib.main.os.scriptgen.action;

import commons.lib.main.os.scriptgen.ScriptPattern;

public class InstallBinary extends ScriptPattern {

    @Override
    public String id() {
        return "installSource";
    }

    public boolean osSpecific() {
        return true;
    }

    @Override
    public String getWindowsCommand() {
        return "install.bat" +
                "\r\n" +
                "mkdir %userprofile%/.tools" +
                "\r\n" +
                "mv ./build/libs/* %userprofile%/.tools/" +
                "\r\n" +
                "set PATH=$PATH:%userprofile%/.tools/" +
                "\r\n" ;
    }

    @Override
    public String getLinuxCommand() {
        return "./install.sh" +
                "\r" +
                "mkdir ~/.tools" +
                "\r" +
                "mv ./build/libs/* ~/.tools/" +
                "\r" +
                "echo PATH=$PATH:~/.tools/ >> .bashrc";
    }
}
