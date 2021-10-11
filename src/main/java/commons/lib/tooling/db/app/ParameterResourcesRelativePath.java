package commons.lib.tooling.db.app;

import commons.lib.main.console.v4.CliParameter;

import java.nio.file.Path;
import java.util.List;

public class ParameterResourcesRelativePath implements CliParameter {

    public static final String PROPERTY_KEY = "resources.relative.path";

    @Override
    public String propertyKey() {
        return PROPERTY_KEY;
    }

    @Override
    public List<String> parameterKeys() {
        return List.of("--resources-rel-path", "-r");
    }

    @Override
    public String defaultValue() {
        return null;
    }

    @Override
    public boolean validate(String value) {
        try {
            Path.of(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String description() {
        return " the relative path (from the project root) to access to the resources.";
    }

    @Override
    public String question() {
        return "What is the relative path (from the project root) to access to the resources ?";
    }

}
