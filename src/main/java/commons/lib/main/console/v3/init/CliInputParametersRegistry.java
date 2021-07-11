package commons.lib.main.console.v3.init;

import commons.lib.tooling.documentation.MdDoc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@MdDoc(description = "This class is responsible to store all the parameters a command line app would accept.")
public class CliInputParametersRegistry {
    public static final Parameter DEFAULT_PARAMETER = new Parameter("", "", "", "");

    private final Map<String, Parameter> parametersPerShortName = new HashMap<>();
    private final Map<String, Parameter> parametersPerProperty = new HashMap<>();

    @MdDoc(description = "Register a command line parameter.")
    public void register(String commandLineKey, String propertyKey, String defaultValue, String description) {
        // TODO app-info_en actually contains localized descriptions
        parametersPerShortName.put(commandLineKey, new Parameter(commandLineKey, propertyKey, defaultValue, description));
        parametersPerProperty.put(propertyKey, new Parameter(commandLineKey, propertyKey, defaultValue, description));
    }

    @MdDoc(description = "lookup for a parameter by its property key. For example : log.verbosity")
    public Optional<Parameter> fromProperty(String property) {
        return Optional.ofNullable(parametersPerProperty.get(property));
    }

    @MdDoc(description = "lookup for a parameter by its command line key. For example : --verbose")
    public Optional<Parameter> fromCommandLineKey(String cmdLine) {
        return Optional.ofNullable(parametersPerShortName.get(cmdLine));
    }

    @MdDoc(description = "Returns a properties file data with the values currently stored in this instance.")
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

    @MdDoc(description = "Provide a string with all possible parameters, ready to be shown to the user.")
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

    @MdDoc(description = "Describe a parameter (name, property key, description)")
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
