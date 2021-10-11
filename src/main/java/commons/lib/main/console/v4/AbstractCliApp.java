package commons.lib.main.console.v4;

import commons.lib.main.SystemUtils;
import commons.lib.main.console.ConsoleFactory;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCliApp {


    public static final CliParameter DEFAULT_NO_USER_INTERACTION_PARAMETER = new CliParameter() {
        @Override
        public String propertyKey() {
            return "no.user.interaction";
        }

        @Override
        public List<String> parameterKeys() {
            return List.of("--no-user-interaction");
        }

        @Override
        public String defaultValue() {
            return "true";
        }

        @Override
        public boolean validate(String value) {
            return Boolean.parseBoolean(value);
        }

        @Override
        public String description() {
            return null;
        }
    };
    private final Map<String, CliParameter> expectedCliParameters = new HashMap<>();

    public Map<String, String> validateInput(String[] userCliParameters) {
        boolean noUserInteraction = false;
        // Look for global parameters (i.e. parameters that would impact the logic for all other parameters)
        for (String userCliParameter : userCliParameters) {
            if (noUserInteractionSystemKey().parameterKeys().contains(userCliParameter)) {
                noUserInteraction = true;
                break;
            }
        }

        // Validate user's command line
        final Map<String, String> userInput = new HashMap<>();
        String lastKeyUsed = null;
        for (int i = 0; i < userCliParameters.length; i++) {
            final String userCliParameterKey = userCliParameters[i];
            final CliParameter cliParameter = expectedCliParameters.get(userCliParameterKey);
            if (cliParameter == null) {
                if (lastKeyUsed == null) {
                    System.err.println("Invalid parameter " + userCliParameterKey);
                    showUsage();
                    SystemUtils.failUser();
                } else {
                    String lastPropertyKeyUsed = expectedCliParameters.get(lastKeyUsed).propertyKey();
                    String s = userInput.get(lastPropertyKeyUsed);
                    if (s == null) {
                        System.err.println("Invalid parameter " + userCliParameterKey);
                        showUsage();
                        SystemUtils.failUser();
                    } else {
                        userInput.put(lastPropertyKeyUsed, s + " " + userCliParameterKey);
                    }
                }
            } else {
                i++;
                if (i < userCliParameters.length) {
                    final String userCliValue = userCliParameters[i];
                    userInput.put(cliParameter.propertyKey(), userCliValue);
                }
                lastKeyUsed = userCliParameterKey;
            }
        }

        // Validate user's parameters
        for (Map.Entry<String, CliParameter> expectedCliParameterEntry : expectedCliParameters.entrySet()) {
            final String userParameterKey = expectedCliParameterEntry.getValue().propertyKey();
            final String userParameter = userInput.get(userParameterKey);
            /*if (userParameter.isEmpty()) {
                String defaultValue = expectedCliParameterEntry.getValue().defaultValue();
                if (defaultValue == null) {
                    System.err.println("The following parameter is mandatory : " + expectedCliParameterEntry.getKey());
                    showUsage();
                    SystemUtils.failUser();
                }
                // expectedCliParameterEntry.getValue().validate(defaultValue)*/
           // } else {
                final boolean isValid = expectedCliParameterEntry.getValue().validate(userParameter);
                if (!isValid) {
                    System.err.println("Invalid parameter " + expectedCliParameterEntry.getKey() + " " + userParameter);
                    showUsage();
                    SystemUtils.failUser();
                }

            //}
        }
        return userInput;
    }

    private void showUsage() {
        for (CliParameter parameter : expectedCliParameters.values()) {
            final String parameterKeys = parameter.parameterKeys().stream().collect(Collectors.joining(", "));
            ConsoleFactory.getInstance().printf(parameterKeys + "\t" + parameter.description() + "\n");
        }
    }

    public void register(List<CliParameter> parameters) {
        parameters.forEach(cliParameter -> cliParameter.parameterKeys().forEach(parameterKey -> expectedCliParameters.put(parameterKey, cliParameter)));
    }

    public static void main(String[] args) {

    }

    public CliParameter noUserInteractionSystemKey() {
        return DEFAULT_NO_USER_INTERACTION_PARAMETER;
    }

}
