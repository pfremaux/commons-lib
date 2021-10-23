package commons.lib.main.filestructure.toml;

import commons.lib.main.filestructure.Configuration;
import commons.lib.main.filestructure.SubConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TomlParser {


    public static Configuration parse(Path tomlFileConfigPath) throws IOException {
        final List<String> tomlLines = Files.readAllLines(tomlFileConfigPath);
        final Map<String, SubConfiguration> subConfigs = new HashMap<>();
        // Looks like the only case of multiline would be an array
        boolean isMultiline = false;
        final StringBuilder multilineBuffer = new StringBuilder();
        SubConfiguration currentSubConfiguration = new SubConfiguration(new HashMap<>());
        String currentKey = "root";
        subConfigs.put(currentKey, currentSubConfiguration);
        for (String line : tomlLines) {
            if (isMultiline) {
                multilineBuffer.append(" ");
                multilineBuffer.append(line);
                if (line.endsWith("]")) {
                    // process multiline
                    final List<String> list = processArray(multilineBuffer.toString());
                    currentSubConfiguration.getConfigData().put(currentKey, new SubConfiguration(list));
                }
                // Now clear the buffer
                multilineBuffer.setLength(0);
                continue;

            }
            final String trimLine = line.trim();
            if (trimLine.startsWith("[")) {
                // Sub config
                currentKey = trimLine.substring(1, trimLine.length() - 1);
                final SubConfiguration previousCurrentSubConfig = currentSubConfiguration;
                currentSubConfiguration = new SubConfiguration(new HashMap<>());
                previousCurrentSubConfig.getConfigData().put(currentKey, currentSubConfiguration);
            } else {
                final int firstEqualIndex = trimLine.indexOf('=');
                if (firstEqualIndex == -1) {
                    System.err.println("Was looking for an '=' for none was found : " + trimLine);
                }
                currentKey = trimLine.substring(0, firstEqualIndex).trim();
                final String value = trimLine.substring(firstEqualIndex).trim();
                if (value.startsWith("[")) {
                    if (!value.endsWith("]")) {
                        isMultiline = true;
                        multilineBuffer.append(line);
                        continue;
                    }
                    final List<String> strings = processArray(value);
                    currentSubConfiguration.getConfigData().put(currentKey, new SubConfiguration(strings));
                } else if (value.startsWith("\"")) {
                    final String stringValue = value.substring(1, value.length() - 1);
                    currentSubConfiguration.getConfigData().put(currentKey, new SubConfiguration(stringValue));
                } else {
                    // TODO test with regex date format
                    currentSubConfiguration.getConfigData().put(currentKey, new SubConfiguration(Long.parseLong(value)));
                }
            }
        }
        return new Configuration(subConfigs);
    }

    private static List<String> processArray(String d) {
        final List<String> values = new ArrayList<>();
        final String withoutBrackets = d.substring(1, d.length() - 1).trim();
        boolean inString = false;
        final StringBuilder builder = new StringBuilder();
        for (char c : withoutBrackets.toCharArray()) {
            if (c == '"') {
                inString = !inString;
                if (!inString) {
                    values.add(builder.toString());
                    builder.setLength(0);
                }
                continue;
            }
            if (inString) {
                builder.append(c);
            }
        }
        if (inString) {
            System.err.println("didn't finish the array parsing outside of a quote. Something went wrong with this array : " + d);
        }
        return values;
    }

}

