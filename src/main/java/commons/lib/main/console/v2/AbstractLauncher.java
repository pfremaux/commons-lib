package commons.lib.main.console.v2;

import commons.lib.main.SystemUtils;
import commons.lib.main.console.v2.action.ActionConsole;
import commons.lib.main.console.v2.action.ActionConsoleProcessor;
import commons.lib.main.console.v2.action.ActionConsoleRepo;
import commons.lib.main.console.v2.action.ActionSummary;
import commons.lib.main.console.v2.parameter.InputParametersContract;
import commons.lib.main.console.v2.parameter.InputParametersRepo;
import commons.lib.main.console.v2.test.ChoiceRepo;
import commons.lib.main.console.v2.yaml.YamlAction;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractLauncher {
    private boolean debugMode;
    private ActionConsole root;

    public void manageArguments(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("-h")) {
                ResourceBundle appInfo;
                try {
                    appInfo = ResourceBundle.getBundle("app-info", Locale.ENGLISH);
                } catch (MissingResourceException e) {
                    appInfo = null;
                }

                if (appInfo == null) {
                    for (InputParametersContract contract : InputParametersRepo.getAll()) {
                        System.out.println("\t"
                                + contract.commandLineKey()
                                + "\t\t" + contract.key()
                                + "default = "
                                + contract.defaultValue());
                    }
                } else {
                    for (InputParametersContract contract : InputParametersRepo.getAll()) {
                        System.out.println("\t"
                                + contract.commandLineKey()
                                + "\t\t"
                                + appInfo.getString(contract.key())
                                + "default = "
                                + contract.defaultValue()
                        );
                    }
                }
                SystemUtils.endOfApp();
            } else {
                String consoleFile = null;
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-c") && i < args.length - 1) {
                        consoleFile = args[i + 1];
                    } else if (args[i].equals("-d")) {
                        debugMode = true;
                    }
                }
                Console.get(Path.of(consoleFile));
            }
        }
    }

    public void registerParameters(InputParametersContract... contracts) {
        for (InputParametersContract contract : contracts) {
            try {
                InputParametersRepo.register(contract);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        InputParametersRepo.lock();
    }

    public void registerChoice(Choice choice) {
        try {
            ChoiceRepo.register(choice);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void registerAction(Function<String, ActionConsole> action) {
        try {
            ActionConsoleRepo.register(action.apply("").getContext(), action);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void registerAction(Function<String, ActionConsole>... actions) {
        try {
            for (Function<String, ActionConsole> action : actions) {
                ActionConsoleRepo.register(action.apply("").getContext(), action);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ActionConsoleRepo.lock();
        for (Map.Entry<Choice, Function<String, ActionConsole>> functionEntry : ActionConsoleRepo.getAll().entrySet()) {
            ActionConsole apply = functionEntry.getValue().apply("");
            registerChoice(apply.getContext());
        }

    }

    public void registerActionProcessor(Choice c, Consumer<ActionConsole> a) {
        try {
            ActionConsoleProcessor.register(c, a);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void lockSettings() {
        InputParametersRepo.lock();
        ActionConsoleRepo.lock();
        ActionConsoleProcessor.lock();
        for (Map.Entry<Choice, Function<String, ActionConsole>> functionEntry : ActionConsoleRepo.getAll().entrySet()) {
            ActionConsole apply = functionEntry.getValue().apply("");
            //registerChoice(apply.getContext());
        }
        //ChoiceRepo.lock();
    }

    protected void run(Choice starting) {
        root = ActionConsoleRepo.get(starting);
        ActionConsoleRepo.root = root;
        root.go();
    }



    protected void register(List<YamlAction> yamlActions) {
        for (YamlAction yamlAction : yamlActions) {
            Choice choice = new Choice(yamlAction.getChoiceId(), yamlAction.getChoiceName());
            try {
                ChoiceRepo.register(choice);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        ChoiceRepo.lock();
        final List<ActionSummary> result = new ArrayList<>();
        for (YamlAction yamlAction : yamlActions) {
            final Choice choice = ChoiceRepo.get(yamlAction.getChoiceId());
            final List<Choice> choices = yamlAction.getSubChoiceList().stream().map(ChoiceRepo::get).collect(Collectors.toList());
            final ActionSummary actionSummary = new ActionSummary(
                    choice,
                    s-> new ActionConsole(
                            choice,
                            yamlAction.toQuestions(),
                            choices
                            ),
                    a -> {}
            );
            result.add(actionSummary);
        }
        register(result.toArray(new ActionSummary[0]));
    }
    protected void register(ActionSummary... settings) {
        for (ActionSummary setting : settings) {
            // registerChoice(setting.getChoice());
            registerAction(setting.getAction());
            registerActionProcessor(setting.getChoice(), setting.getPostOperation());
        }
        lockSettings();
    }
}
