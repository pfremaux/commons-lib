package commons.lib.main.console.v4;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.CustomConsole;

import java.util.List;

public interface CliParameter {

    String propertyKey();
    List<String> parameterKeys();
    String defaultValue();
    boolean validate(String value);

    String description();
    default String question() {
        return description() + " ? ";
    }
    default String ask() {
        final CustomConsole customConsole = ConsoleFactory.getInstance();
        String response = null;
        Boolean isValid = null;
        do {
            if (isValid != null && !isValid) {
                customConsole.printf("Validation failed.");
            }
            customConsole.printf(question());
            response = customConsole.readLine();
            isValid = validate(response);
        } while (isValid);

        return response;
    }

}
