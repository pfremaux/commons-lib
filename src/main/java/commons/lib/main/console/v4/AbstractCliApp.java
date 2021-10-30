package commons.lib.main.console.v4;

import commons.lib.main.SystemUtils;
import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.filestructure.SubConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractCliApp {


    private final Map<String, CliParameter> expectedCliParameters = new HashMap<>();

    public Map<String, String> validateInput(String[] userCliParameters) {
        boolean noUserInteraction = false;
        boolean generateScripts = false;
        boolean generateToml = false;
        // Look for global parameters (i.e. parameters that would impact the logic for all other parameters)
        for (String userCliParameter : userCliParameters) {
            if (DefaultParameters.DEFAULT_NO_USER_INTERACTION_PARAMETER.parameterKeys().contains(userCliParameter)) {
                noUserInteraction = true;
                break;
            } else if (DefaultParameters.DEFAULT_GENERATE_SCRIPTS_PARAMETER.parameterKeys().contains(userCliParameter)) {
                generateScripts = true;
                break;
            } else if (DefaultParameters.DEFAULT_GENERATE_TOML_PARAMETER.parameterKeys().contains(userCliParameter)) {
                generateToml = true;
                break;
            }
        }

        // Validate user's command line
        final Map<String, String> userInputs = new HashMap<>();
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
                    String s = userInputs.get(lastPropertyKeyUsed);
                    if (s == null) {
                        System.err.println("Invalid parameter " + userCliParameterKey);
                        showUsage();
                        SystemUtils.failUser();
                    } else {
                        userInputs.put(lastPropertyKeyUsed, s + " " + userCliParameterKey);
                    }
                }
            } else {
                i++;
                if (i < userCliParameters.length) {
                    final String userCliValue = userCliParameters[i];
                    userInputs.put(cliParameter.propertyKey(), userCliValue);
                }
                lastKeyUsed = userCliParameterKey;
            }
        }

        if (generateScripts) {
            final String destinationConfigFile = "~/.bash_profile";
            final String newLine = "\n";
            final StringBuilder linuxOsScriptBuilder = new StringBuilder();
            linuxOsScriptBuilder.append("#!/bin/bash");
            linuxOsScriptBuilder.append(newLine);
            for (Map.Entry<String, CliParameter> entry : expectedCliParameters.entrySet()) {
                final String parameterName = entry.getKey();
                linuxOsScriptBuilder.append("export");
                linuxOsScriptBuilder.append(parameterName);

                final String userInputValue = userInputs.get(parameterName);
                if (userInputValue == null) {
                    linuxOsScriptBuilder.append("=TODO_");
                    linuxOsScriptBuilder.append(parameterName);
                } else {
                    linuxOsScriptBuilder.append(userInputValue);
                }
                linuxOsScriptBuilder.append(" >> ");
                linuxOsScriptBuilder.append(destinationConfigFile);
                linuxOsScriptBuilder.append(newLine);
            }
            final String fileData = linuxOsScriptBuilder.toString();
            SystemUtils.endOfApp();
        }

        if (generateToml) {
            SubConfiguration subConfiguration = new SubConfiguration(null, new HashMap<>());
            SubConfiguration currentAppConfig = new SubConfiguration(subConfiguration, new HashMap<>());
            subConfiguration.getConfigData().put("todoAppName", currentAppConfig);
            for (Map.Entry<String, CliParameter> entry : expectedCliParameters.entrySet()) {
                final String parameterName = entry.getKey();
                SubConfiguration configValue = new SubConfiguration(
                        currentAppConfig,
                        Objects.requireNonNullElse(userInputs.get(parameterName),
                                Objects.requireNonNullElse(entry.getValue().defaultValue(), "TODO_" + parameterName)));
                currentAppConfig.getConfigData().put(parameterName, configValue);
            }
            for (Map.Entry<String, SubConfiguration> entry : subConfiguration.getConfigData().entrySet()) {
                if (entry.getValue().getConfigData().isEmpty() && entry.getValue().hasValue()) {
                    System.out.println(entry.getKey() + " = " + entry.getValue().smartGet());
                } else if (!entry.getValue().getConfigData().isEmpty()) {
                    System.out.println("[" + entry.getKey() + "]");
                    for (Map.Entry<String, SubConfiguration> entry2 : entry.getValue().getConfigData().entrySet()) {
                        System.out.println(entry2.getKey() + " = " + entry2.getValue().smartGet());
                    }
                }
                System.out.println();
            }
            SystemUtils.endOfApp();
        }

        // Validate user's parameters
        for (Map.Entry<String, CliParameter> expectedCliParameterEntry : expectedCliParameters.entrySet()) {
            final String userParameterKey = expectedCliParameterEntry.getValue().propertyKey();
            final String userParameter = userInputs.get(userParameterKey);
            if (userParameter == null || userParameter.isEmpty()) {
                String defaultValue = expectedCliParameterEntry.getValue().defaultValue();
                if (defaultValue == null) {
                    System.err.println("The following parameter is mandatory : " + expectedCliParameterEntry.getKey());
                    showUsage();
                    SystemUtils.failUser();
                }
                if (!expectedCliParameterEntry.getValue().validate(defaultValue)) {
                    System.err.println("The following parameter is mandatory : " + expectedCliParameterEntry.getKey());
                    showUsage();
                    SystemUtils.failUser();
                }
                final boolean isValid = expectedCliParameterEntry.getValue().validate(userParameter);
                if (!isValid) {
                    System.err.println("Invalid parameter " + expectedCliParameterEntry.getKey() + " " + userParameter);
                    showUsage();
                    SystemUtils.failUser();
                }
            }
        }
        return userInputs;
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

}
