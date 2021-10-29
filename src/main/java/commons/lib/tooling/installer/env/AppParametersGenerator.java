package commons.lib.tooling.installer.env;

import java.util.Map;

public class AppParametersGenerator {

    public void description() {
        System.out.println("We have multiple layers :");
        System.out.println("- Master OS : environment variables + config files,");
        // More info here https://stackoverflow.com/questions/42297387/docker-build-with-build-arg-with-multiple-arguments
        // More info here https://stackoverflow.com/questions/32727594/how-to-pass-arguments-to-shell-script-through-docker-run
        System.out.println("- Container : config file. Can take values in parameter with 'docker build' and 'docker run',");
        System.out.println("- Running app : takes parameters defined above + config files.");
    }

    public static void masterOsInitScript(StringBuilder b, Map<String, String> config) {
        for (Map.Entry<String, String> entry : config.entrySet()) {
            b.append("echo \"set ").append(entry.getKey()).append("=").append(entry.getValue());
            b.append("\" >> ~/.bashrc\n");
        }
    }

    public static void containerInitOnBuild(StringBuilder b, Map<String, String> config) {
        for (Map.Entry<String, String> entry : config.entrySet()) {
            b.append("echo \"ARG ").append(entry.getKey());
            b.append("\" >> Dockerfile\n");
        }
    }

    public static void containerInitOnRun(StringBuilder b, String scriptRunnerName) {
        b.append("echo \"COPY ./");
        b.append(scriptRunnerName);
        b.append(" /\" >> Dockerfile\n");
        b.append("echo \"");
        b.append("ENTRYPOINT [\"/");
        b.append(scriptRunnerName);
        b.append("\"]");
        b.append("\" >> Dockerfile\n");
    }

    public static void scriptRunnerCreate(StringBuilder b, Map<String, String> config, String scriptRunnerName) {
        b.append("echo \"");
        for (Map.Entry<String, String> entry : config.entrySet()) {
            b.append(" -D");
            b.append(entry.getKey());
            b.append("=");
            b.append(entry.getValue());
        }
        b.append("\"");
        b.append(" >>");
        b.append(scriptRunnerName);
        b.append("\n");
    }

    public static class AppInputParameters {
        private final Map<String, String> inputs;

        public AppInputParameters(Map<String, String> inputs) {
            this.inputs = inputs;
        }

        public Map<String, String> getInputs() {
            return inputs;
        }
    }

}
