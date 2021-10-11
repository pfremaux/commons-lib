package commons.lib.tooling.db.app;

import commons.lib.main.console.v4.CliParameter;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class ParameterDomainClassPackage implements CliParameter {

    public static final String PROPERTY_KEY = "domain.class.package";
    private static final Pattern pattern = Pattern.compile("[a-z]+[a-z\\.]*[a-z]+");

    @Override
    public String propertyKey() {
        return PROPERTY_KEY;
    }

    @Override
    public List<String> parameterKeys() {
        return List.of("--domain-package", "-d");
    }

    @Override
    public String defaultValue() {
        return null;
    }

    @Override
    public boolean validate(String value) {
        try {
            return pattern.matcher(value).find();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String description() {
        return "Package where domain class will be stored.";
    }

    @Override
    public String question() {
        return "What's the package where domain classes will be generated ?";
    }

}
