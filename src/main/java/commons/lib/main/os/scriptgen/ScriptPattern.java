package commons.lib.main.os.scriptgen;

public abstract class ScriptPattern {

    private final String[] parameters;


    public abstract String id();

    public ScriptPattern(String... parameters) {
        this.parameters = parameters;
    }

    public boolean osSpecific() {
        return false;
    }

    public String getWindowsCommand() {
        return getCommand();
    }

    public String getLinuxCommand() {
        return getCommand();
    }

    public String getCommand() {
        if (osSpecific()) {
            throw new UnsupportedOperationException("The script pattern " + id() + " need to be overrode.");
        }
        return "cd";
    }

    public String[] getParameters() {
        return parameters;
    }
}
