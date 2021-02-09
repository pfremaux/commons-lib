package commons.lib.main.console.v3.init;

import commons.lib.main.SystemUtils;
import commons.lib.main.console.v3.AppInfo;
import commons.lib.main.os.CommandLineExecutor;
import commons.lib.main.os.CommandStatus;
import commons.lib.tooling.documentation.MdDoc;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@MdDoc(description = "Extend this abstract class if you're creating a CLI app. " +
        "The principe of this class is to set the provided parameters in the properties system of the JVM. For example :" +
        "java -jar cmd.jar -w \"hello !\"" +
        "You can save the 'hello !' and load it back thanks to System.getProperty(..)")
public abstract class CliApp {

    private CliInputParametersRegistry cliInputParametersRegistry = new CliInputParametersRegistry();
    private AppInfo appInfo = new AppInfo();

    @MdDoc(description = "Override this method to register all parameters you want to process. Call the method register(..) for each parameters." +
            " For example : register(\"-w\", \"welcome.message\", \"Hello !\", \"Set a welcome message.\");")
    public abstract void init();

    @MdDoc(description = "Register a parameter your app can accept in command line.")
    public void register(
            @MdDoc(description = "Command line key the user need to add in order to indicate which parameter he is setting. For example -w") String commandLineKey,
            @MdDoc(description = "Property key you want to link the parameter with. For example : my.welcome.message") String propertyKey,
            @MdDoc(description = "What default value you want to have if the user doesn't provide a value. For example : Hello") String defaultValue,
            @MdDoc(description = "Description of the parameter. If the command line fails the app will exit with a description of all parameters.") String description) {
        cliInputParametersRegistry.register(commandLineKey, propertyKey, defaultValue, description);
    }

    public void validateAndLoad(String... args) {
        init();
        processParameters(args);
        final String githubUrl = appInfo.getGithubUrl();
        if (githubUrl != null) {
            cliInputParametersRegistry.register(
                    "glv",
                    "get.latest.version",
                    "false",
                    "Download the source code, compile and install (experimental)");
        }
    }

    public String getValueWithCommandLine(String cmd) {
        return cliInputParametersRegistry.fromCommandLineKey(cmd).orElse(CliInputParametersRegistry.DEFAULT_PARAMETER).getPropertyString();
    }

    private void processParameters(String... args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("-h")) {
                showUsage(appInfo);
                SystemUtils.endOfApp();
            } else if (args[0].equalsIgnoreCase("glv")) {
                CommandLineExecutor commandLineExecutor = new CommandLineExecutor();
                try {
                    CommandStatus execution = commandLineExecutor.execute("git clone " + appInfo.getGithubUrl());
                    commandLineExecutor.waitAllCommands(10000L);
                    CommandLineExecutor.validateEndOfExecution(execution);
                    execution = commandLineExecutor.execute("./" + appInfo.getProjectName() + "/install.sh");
                    commandLineExecutor.waitAllCommands(10000L);
                    CommandLineExecutor.validateEndOfExecution(execution);

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            } else{
                for (int i = 0; i < args.length; i = i + 2) {
                    final Optional<CliInputParametersRegistry.Parameter> inputParameter = cliInputParametersRegistry.fromCommandLineKey(args[i]);
                    if (inputParameter.isPresent()) {
                        System.setProperty(inputParameter.get().getKey(), args[i + 1]);
                    } else {
                        if (!args[i].startsWith("-D")) {
                            System.err.println("Unexpected parameter : " + args[i]);
                            SystemUtils.failUser();
                        }
                    }
                }
            }
        } else {
            showUsage(appInfo);
            SystemUtils.endOfApp();
        }
    }

    private void showUsage(AppInfo appInfo) {
        if (appInfo.isInitialized()) {
            final String version = appInfo.getVersion();// TODO gerer le versioning pour de vrai....
            System.out.println("Version : " + version);
        }
        System.out.println("Example : ");
        System.out.println("<this app> " + cliInputParametersRegistry.toCommandLineFormat());
        for (CliInputParametersRegistry.Parameter value : cliInputParametersRegistry.values()) {
            System.out.println("\t" + value.getCommandLineKey() + "\t\t" + cliInputParametersRegistry.fromProperty(value.getKey()).orElse(CliInputParametersRegistry.DEFAULT_PARAMETER).getDescription());
        }
    }
}
