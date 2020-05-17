package commons.lib.generator.sql2Java;


import java.nio.file.Path;
import java.nio.file.Paths;

public enum InputParameter {
    DB_FILE_NAME_DIRECTORY_FULL_PATH("db.main.dir", "db", ""),
    DB_FILE_NAMES("db.file.names", "data-{}.db", ""),
    PARENT_PACKAGE("db.parent.package", "generated", ""),
    CREATION_SCRIPT_PATH("db.input.dir.script", "input", ""),
    INPUT_TYPE("db.input.type", "creationScript", ""),
    DB_TYPE("db.type", "sqlite", "--db-type");

    String key;
    String defaultValue;
    String commandLineKey;

    InputParameter(String key, String defaultValue, String commandLineKey) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.commandLineKey = commandLineKey;
    }

    public String getPropertyString() {
        return System.getProperty(key, defaultValue);
    }

    public static String getPropertyString(InputParameter input) {
        return System.getProperty(input.key, input.defaultValue);
    }

    public int getPropertyInt() {
        return Integer.parseInt(System.getProperty(key, defaultValue));
    }

    public Path getPropertyPath() {
        return Paths.get(System.getProperty(key, defaultValue));
    }

    public static String toPropertiesFileFormat() {
        final StringBuffer buffer = new StringBuffer();
        for (InputParameter inputParameter : values()) {
            String keyName = inputParameter.key;
            String value = getPropertyString(inputParameter);
            buffer.append(keyName);
            buffer.append(" = ");
            buffer.append(value);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public static String toCommandLineFormat() {
        final StringBuffer buffer = new StringBuffer();
        for (InputParameter inputParameter : values()) {
            String keyName = inputParameter.commandLineKey;
            if (keyName == null) {
                continue;
            }
            String value = getPropertyString(inputParameter);
            buffer.append(keyName);
            buffer.append("=");
            buffer.append(value);
            buffer.append(" ");
        }
        return buffer.toString();
    }

}
