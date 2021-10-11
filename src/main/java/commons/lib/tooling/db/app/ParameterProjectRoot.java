package commons.lib.tooling.db.app;

import commons.lib.main.console.v4.CliParameter;

import java.nio.file.Path;
import java.util.List;

public class ParameterProjectRoot implements CliParameter {

    public static final String PROPERTY_KEY = "project.root.path";

    @Override
    public String propertyKey() {
        return PROPERTY_KEY;
    }

    @Override
    public List<String> parameterKeys() {
        return List.of("--project-path", "-p");
    }

    @Override
    public String defaultValue() {
        return null;
    }

    @Override
    public boolean validate(String value) {
        try {
            return Path.of(value).toFile().exists();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String description() {
        return "Targeted project path on which the tool will work.";
    }

    @Override
    public String question() {
        return "What's the root path ?";
    }

}
