package commons.lib.main.console.v2.parameter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public interface InputParametersContract {

    static String getPropertyString(InputParametersContract input) {
        return System.getProperty(input.key(), input.defaultValue());
    }

    default int getPropertyInt() {
        return Integer.parseInt(System.getProperty(key(), defaultValue()));
    }

    static Optional<InputParametersContract> fromCommandLineKey(String cmdLine) {
        for (InputParametersContract parameter : InputParametersRepo.getAll()) {
            if (parameter.commandLineKey().equals(cmdLine)) {
                return Optional.of(parameter);
            }
        }
        return Optional.empty();
    }

    static String toPropertiesFileFormat() {
        final StringBuilder buffer = new StringBuilder();
        for (InputParametersContract inputParameter : InputParametersRepo.getAll()) {
            String keyName = inputParameter.key();
            String value = getPropertyString(inputParameter);
            buffer.append(keyName);
            buffer.append(" = ");
            buffer.append(value);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    static String toCommandLineFormat() {
        final StringBuilder buffer = new StringBuilder();
        for (InputParametersContract inputParameter : InputParametersRepo.getAll()) {
            String keyName = inputParameter.commandLineKey();
            if (keyName == null) {
                continue;
            }
            String value = getPropertyString(inputParameter);
            buffer.append(keyName);
            buffer.append(" ");
            buffer.append(value);
            buffer.append(" ");
        }
        return buffer.toString();
    }

    default String getPropertyString() {
        return System.getProperty(key(), defaultValue());
    }

    default Path getPropertyPath() {
        String property = System.getProperty(key(), defaultValue());
        if (property.length() == 0) {
            return null;
        }
        return Paths.get(property);
    }

    String defaultValue();

    String key();

    String commandLineKey();

}
