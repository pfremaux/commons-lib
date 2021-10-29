package commons.lib.main.filestructure.toml;

import commons.lib.main.filestructure.SubConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TomlFileParserNoInheritance {
    private final Map<Pattern, BiFunction<String, SubConfiguration, SubConfiguration>> rules = new HashMap<>();

    public void registerRule(Pattern regex, BiFunction<String,  SubConfiguration, SubConfiguration> config) {
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

    public static void main(String[] args) throws IOException {
        String path = "/home/shinichi/IdeaProjects/commons-lib/src/main/resources/simpleConfig.toml";
        TomlFileParserNoInheritance textFileParser = new TomlFileParserNoInheritance();
        // TITLES
        Pattern title = Pattern.compile("^\\[[a-z0-9\\-]+\\]$");
        textFileParser.registerRule(title, (line, config) -> {
            final String titleStr = line.substring(1, line.length() - 1);
            SubConfiguration parent = config.getParent();
            final SubConfiguration subConfiguration;
            if (parent != null) {
                subConfiguration = new SubConfiguration(parent, new HashMap<>());
            }  else {
                subConfiguration = new SubConfiguration(config, new HashMap<>());
            }
            config.getConfigData().put(titleStr, subConfiguration);
            return subConfiguration;
        });

        // KEY VALUE STRING
        Pattern keyValue = Pattern.compile("^[a-z0-9 ]+=[ ]*\"+.+\"+$", Pattern.CASE_INSENSITIVE);
        textFileParser.registerRule(keyValue, (line, config) -> {
            int equalIndex = line.indexOf("=");
            String key = line.substring(0, equalIndex-1).trim();
            String value = line.substring(equalIndex+1).trim();
            config.getConfigData().put(key, new SubConfiguration(config, value));
            return config;
        });

        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_OFFSET_DATE_TIME).toFormatter();
        // KEY VALUE LONG DATE
        //1979-05-27T07:32:00-08:00
        Pattern keyValueDate = Pattern.compile("^[a-z0-9 ]+=[ ]*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}-\\d{2}:\\d{2}$", Pattern.CASE_INSENSITIVE);
        textFileParser.registerRule(keyValueDate, (line, config) -> {
            int equalIndex = line.indexOf("=");
            String key = line.substring(0, equalIndex-1).trim();
            String value = line.substring(equalIndex+1).trim();
            TemporalAccessor date = dateTimeFormatter.parse(value);
            config.getConfigData().put(key, new SubConfiguration(config, date));
            return config;
        });

        // ARRAY NUMBERS
        Pattern arrayInts = Pattern.compile("^[a-z0-9 ]+=[ ]*\\[[0-9, ]+\\]$", Pattern.CASE_INSENSITIVE);
        textFileParser.registerRule(arrayInts, (line, config) -> {
            String trimLine =  line.trim() ;
            int equalIndex = trimLine.indexOf("=");
            String key = trimLine.substring(0, equalIndex-1).trim();
            String value = trimLine.substring(equalIndex+3, trimLine.length()-1);
            List<Long> array = Arrays.stream(value.split(",")).map(String::trim).map(Long::parseLong).collect(Collectors.toList());
            config.getConfigData().put(key, new SubConfiguration(config, array));
            return config;
        });

        // ARRAY STRINGS
        Pattern arrayStrings = Pattern.compile("^[a-z0-9 ]+=[ ]*\\[(\".*\")*\\]$", Pattern.CASE_INSENSITIVE);
        textFileParser.registerRule(arrayStrings, (line, config) -> {
            String trimLine =  line.trim() ;
            int equalIndex = trimLine.indexOf("=");
            String key = trimLine.substring(0, equalIndex-1).trim();
            String value = trimLine.substring(equalIndex+3, trimLine.length()-1);
            List<String> array = Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList());
            config.getConfigData().put(key, new SubConfiguration(config, array));
            return config;
        });

    }

}
