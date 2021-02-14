package commons.lib.main.os.scriptgen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ScriptGenerator {

    private final boolean isUnix;
    private final List<ScriptPattern> scriptPatternList;

    public ScriptGenerator() {
        this.scriptPatternList = new ArrayList<>();
        this.isUnix = System.lineSeparator().equals("\r");
    }

    public void addInstruction(ScriptPattern scriptPattern) {
        scriptPatternList.add(scriptPattern);
    }

    public boolean isUniversal() {
        boolean isUniversal = true;
        for (ScriptPattern scriptPattern : scriptPatternList) {
            isUniversal &= !scriptPattern.osSpecific();
        }
        return isUniversal;
    }

    public String getSourceScript() {
        StringBuilder buffer = new StringBuilder();
        if (isUniversal()) {
            for (ScriptPattern scriptPattern : scriptPatternList) {
                buffer.append("!#/bin/bash");
                buffer.append(scriptPattern.getCommand());
            }
        } else if (isUnix) {
            buffer.append("!#/bin/bash");
            for (ScriptPattern scriptPattern : scriptPatternList) {
                buffer.append(scriptPattern.getLinuxCommand());
            }
        } else {
            for (ScriptPattern scriptPattern : scriptPatternList) {
                buffer.append(scriptPattern.getWindowsCommand());
            }
        }
        return buffer.toString();
    }

    public void generateFile(String filename) {
        String wholeScript = getSourceScript();
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(filename))) {
            bufferedWriter.append(wholeScript);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getScriptExtension() {
        return isUniversal()? ".sh.bat" : isUnix ? ".sh" : ".bat";
    }
}
