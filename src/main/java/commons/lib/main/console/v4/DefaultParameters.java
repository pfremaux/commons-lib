package commons.lib.main.console.v4;

import java.util.List;

public class DefaultParameters {
    private DefaultParameters() {

    }



    public static final CliParameter DEFAULT_GENERATE_SCRIPTS_PARAMETER = new CliParameter() {
        @Override
        public String propertyKey() {
            return "GENERATE_SCRIPTS";
        }

        @Override
        public List<String> parameterKeys() {
            return List.of("--gen-scripts");
        }

        @Override
        public String defaultValue() {
            return "false";
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

    public static final CliParameter DEFAULT_GENERATE_TOML_PARAMETER = new CliParameter() {
        @Override
        public String propertyKey() {
            return "GENERATE_TOML";
        }

        @Override
        public List<String> parameterKeys() {
            return List.of("--gen-toml");
        }

        @Override
        public String defaultValue() {
            return "false";
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

    public static final List<CliParameter> ALL_DEFAULT_PARAMETERS = List.of(DEFAULT_GENERATE_TOML_PARAMETER, DEFAULT_GENERATE_SCRIPTS_PARAMETER, DEFAULT_NO_USER_INTERACTION_PARAMETER);
}
