package commons.lib.main.filestructure.toml;

import commons.lib.main.StringUtils;
import commons.lib.main.filestructure.Configuration;
import commons.lib.main.filestructure.SubConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TomlParserLegacy {

    public static void main(String[] args) throws IOException {
        String path = "/home/shinichi/IdeaProjects/commons-lib/src/main/resources/config.toml";
        Configuration parse = parse(Path.of(path));
        SubConfiguration root = parse.getSubConfiguration("root");
        System.out.println(root.getSubConfiguration("database").getSubConfiguration("ports").getStringList());
        System.out.println(root.getSubConfiguration("database").getSubConfiguration("data").getStringList());
        SortedMap<String, SubConfiguration> servers = root.findAllStartingWith("servers");
        for (Map.Entry<String, SubConfiguration> entry : servers.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    public static Configuration parse(Path tomlFileConfigPath) throws IOException {
        final List<String> tomlLines = Files.readAllLines(tomlFileConfigPath);
        Map<String, SubConfiguration> rootMap = new HashMap<>();
        // Looks like the only case of multiline would be an array
        boolean isMultiline = false;
        final StringBuilder multilineBuffer = new StringBuilder();
        SubConfiguration currentSubConfiguration = new SubConfiguration(null, new HashMap<>());
        rootMap.put("root", currentSubConfiguration);
        String currentKey = "root";
        for (String line : tomlLines) {
            if (isMultiline) {
                multilineBuffer.append(" ");
                multilineBuffer.append(line);
                if (line.endsWith("]")) {
                    // process multiline
                    final List<String> list = processArray(multilineBuffer.toString());
                    currentSubConfiguration.getConfigData().put(currentKey, new SubConfiguration(currentSubConfiguration, list));
                }
                // Now clear the buffer
                multilineBuffer.setLength(0);
                continue;

            }
            final String trimLine = line.trim();
            if (trimLine.length() == 0) {
                continue;
            }

            if (trimLine.startsWith("#")) {
                continue;
            } else if (trimLine.startsWith("[")) {
                SubConfiguration parent = currentSubConfiguration.getParent();
                // Sub config
                currentKey = trimLine.substring(1, trimLine.length() - 1);
                //currentSubConfiguration = previousCurrentSubConfig;
                //previousCurrentSubConfig = currentSubConfiguration;
                if (parent != null) {
                    currentSubConfiguration = parent ; //new SubConfiguration(previousCurrentSubConfig, new HashMap<>());
                }
                final SubConfiguration childconfig = new SubConfiguration(currentSubConfiguration, new HashMap<>());
                currentSubConfiguration.getConfigData().put(currentKey, childconfig);
                currentSubConfiguration = childconfig;
            } else {
                final int firstEqualIndex = trimLine.indexOf('=');
                if (firstEqualIndex == -1) {
                    System.err.println("Was looking for an '=' for none was found : " + trimLine);
                }
                currentKey = trimLine.substring(0, firstEqualIndex).trim();
                final String value = trimLine.substring(firstEqualIndex+1).trim();
                if (value.startsWith("[")) {
                    if (!value.endsWith("]")) {
                        isMultiline = true;
                        multilineBuffer.append(line);
                        continue;
                    }
                    final List<String> strings = processArray(value);
                    currentSubConfiguration.getConfigData().put(currentKey, new SubConfiguration(currentSubConfiguration, strings));
                } else if (value.startsWith("\"")) {
                    final String stringValue = value.substring(1, value.length() - 1);
                    currentSubConfiguration.getConfigData().put(currentKey, new SubConfiguration(currentSubConfiguration, stringValue));
                }  else if (value.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}-[0-9]{2}:[0-9]{2}")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS-00:00");
//1979-05-27T07:32:00-08:00
                        // TODO improve regex date format
                } else if (List.of("true", "false").contains (value)) {
                    currentSubConfiguration.getConfigData().put(currentKey, new SubConfiguration(currentSubConfiguration, Boolean.parseBoolean(value)));
                } else {
                    currentSubConfiguration.getConfigData().put(currentKey, new SubConfiguration(currentSubConfiguration, Long.parseLong(value)));

                }
            }
        }

        return new Configuration(rootMap);
    }

    private static List<String> processArray(String d) {
        final List<String> values = new ArrayList<>();
        final String withoutBrackets = d.substring(1, d.length() - 1).trim();
        boolean inString = false;
        boolean inNumber = false;
        final StringBuilder builder = new StringBuilder();
        {
            for (char c : withoutBrackets.toCharArray()) {
                if (c == '"') {
                    inNumber = false;
                    inString = !inString;
                    if (!inString) {
                        values.add(builder.toString());
                        builder.setLength(0);
                    } else if (inNumber) {
                        values.add(builder.toString());
                        builder.setLength(0);
                    }
                    continue;
                }
                if (inString) {
                    builder.append(c);
                } else if (Character.isDigit(c) || c == '.') {
                    builder.append(c);
                    inNumber = true;
                } else if (c == ',' && inNumber) {
                    inNumber = false;
                    values.add(builder.toString());
                    builder.setLength(0);
                }
            }
            if (inString) {
                System.err.println("didn't finish the array parsing outside of a quote. Something went wrong with this array : " + d);
            }
        }

        return values;
    }

}

