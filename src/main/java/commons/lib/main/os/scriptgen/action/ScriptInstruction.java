package commons.lib.main.os.scriptgen.action;

import commons.lib.main.os.scriptgen.ScriptPattern;

public class ScriptInstruction {
    private final ScriptPattern scriptPattern;
    private final String[] parameters;

    public ScriptInstruction(ScriptPattern scriptPattern, String[] parameters) {
        this.scriptPattern = scriptPattern;
        this.parameters = parameters;
    }

    public ScriptPattern getScriptPattern() {
        return scriptPattern;
    }

    public String[] getParameters() {
        return parameters;
    }
}
