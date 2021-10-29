package commons.lib.main.filestructure.toml;

import commons.lib.main.filestructure.SubConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public abstract class   TextFileParser {
    protected final Map<Pattern, BiFunction<String, SubConfiguration, SubConfiguration>> rules = new HashMap<>();

    public final void registerRule(Pattern regex, BiFunction<String,  SubConfiguration, SubConfiguration> config) {
        rules.put(regex, config);
    }

    public void parse(Path tomlFileConfigPath) throws IOException {
        final List<String> tomlLines = Files.readAllLines(tomlFileConfigPath);
        final Map<String, SubConfiguration> configData = new HashMap<>();
        final SubConfiguration root = new SubConfiguration(null, configData);
        SubConfiguration currentSubConfiguration = root;
        Pattern arrayMultiLineStart = Pattern.compile("^[a-z0-9 ]+=[ ]*\\[.*[^\\]]$", Pattern.CASE_INSENSITIVE);
        Pattern arrayMultiLineEnd = Pattern.compile("^.*\\]$", Pattern.CASE_INSENSITIVE);
        StringBuilder multimeBuilder = new StringBuilder();
        for (String line : tomlLines) {
            String workingLine;
            if (arrayMultiLineStart.matcher(line).matches()) {
                multimeBuilder.append(line.trim());
                continue;
            }
            if (multimeBuilder.length() > 0) {
                if (arrayMultiLineEnd.matcher(line).matches()) {
                    multimeBuilder.append(line.trim());
                    workingLine = multimeBuilder.toString();
                    multimeBuilder.setLength(0);
                } else {
                    multimeBuilder.append(line.trim());
                    continue;
                }
            } else {
                workingLine = line;
            }
            for (Map.Entry<Pattern, BiFunction<String, SubConfiguration, SubConfiguration>> entry : rules.entrySet()) {
                if (entry.getKey().matcher(workingLine).matches()) {
                    currentSubConfiguration = entry.getValue().apply(workingLine, currentSubConfiguration);
                    break;
                }
            }
        }
        for (Map.Entry<String, SubConfiguration> entry : root.getConfigData().entrySet()) {
            if (entry.getValue().getConfigData().isEmpty() && entry.getValue().hasValue()) {
                System.out.println(entry.getKey() + " = " + entry.getValue().smartGet());
            }  else if (!entry.getValue().getConfigData().isEmpty()) {
                System.out.println("["+entry.getKey()+"]");
                for (Map.Entry<String, SubConfiguration> entry2 : entry.getValue().getConfigData().entrySet()) {
                    System.out.println(entry2.getKey() + " = " + entry2.getValue().smartGet());
                }
            }
            System.out.println();
            //System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }



}
