package commons.lib.main.console.v3.init;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CliInputParametersRegistry {
    public static final Parameter DEFAULT_PARAMETER = new Parameter("", "", "", "");

    private final Map<String, Parameter> parametersPerShortName = new HashMap<>();
    private final Map<String, Parameter> parametersPerProperty = new HashMap<>();


    public void register(String commandLineKey, String propertyKey, String defaultValue, String description) {
        // TODO app-info_en actually contains localized descriptions
        parametersPerShortName.put(commandLineKey, new Parameter(commandLineKey, propertyKey, defaultValue, description));
        parametersPerProperty.put(propertyKey, new Parameter(commandLineKey, propertyKey, defaultValue, description));
    }



    public Optional<Parameter> fromProperty(String property) {
        return Optional.ofNullable(parametersPerProperty.get(property));
    }

    public Optional<Parameter> fromCommandLineKey(String cmdLine) {
        return Optional.ofNullable(parametersPerShortName.get(cmdLine));
    }

    public String toPropertiesFileFormat() {
        final StringBuilder buffer = new StringBuilder();
        for (Parameter inputParameter : parametersPerShortName.values()) {
            String keyName = inputParameter.key;
            String value = inputParameter.getPropertyString();
            buffer.append(keyName);
            buffer.append(" = ");
            buffer.append(value);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public String toCommandLineFormat() {
        final StringBuilder buffer = new StringBuilder();
        for (Parameter inputParameter : parametersPerShortName.values()) {
            String keyName = inputParameter.commandLineKey;
            if (keyName == null) {
                continue;
            }
            String value = inputParameter.getPropertyString();
            buffer.append(keyName);
            buffer.append(" \"");
            buffer.append(value);
            buffer.append("\" ");
        }
        return buffer.toString();
    }

    public Collection<Parameter> values() {
        return this.parametersPerShortName.values();
    }


    public static class Parameter {
        private final String commandLineKey;
        private final String key;
        private final String defaultValue;
        private final String description;

        public Parameter(String commandLineKey, String key, String defaultValue, String description) {
            this.commandLineKey = commandLineKey;
            this.key = key;
            this.defaultValue = defaultValue;
            this.description = description;
        }

        public String getPropertyString() {
            return System.getProperty(key, defaultValue);
        }


        public int getPropertyInt() {
            return Integer.parseInt(System.getProperty(key, defaultValue));
        }

        public Path getPropertyPath() {
            return Paths.get(System.getProperty(key, defaultValue));
        }

        public String getCommandLineKey() {
            return commandLineKey;
        }

        public String getKey() {
            return key;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getDescription() {
            return description;
        }
    }

}
