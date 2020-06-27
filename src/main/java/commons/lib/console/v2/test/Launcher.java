package commons.lib.console.v2.test;

import commons.lib.console.As;
import commons.lib.console.v2.AbstractLauncher;
import commons.lib.console.v2.Choice;
import commons.lib.console.v2.Console;
import commons.lib.console.v2.ConsoleCache;
import commons.lib.console.v2.action.ActionConsole;
import commons.lib.console.v2.action.ActionSummary;
import commons.lib.console.v2.question.Question;
import commons.lib.console.v2.action.PostProcessorType;
import commons.lib.console.v2.yaml.YamlAction;
import commons.lib.console.v2.yaml.YamlPostProcessor;
import commons.lib.console.v2.yaml.YamlQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Launcher extends AbstractLauncher {

    private static List<YamlAction> readYaml() {
        List<YamlAction> result = new ArrayList<>();
        result.add(
                new YamlAction(
                        "askPren",
                        "Set first name",
                        Arrays.asList(new YamlQuestion("Give your first name.")),
                        Arrays.asList("askPPhra", "showPhra"),
                        new YamlPostProcessor("prenom", PostProcessorType.SAVE_CACHE)
                ));

        result.add(
                new YamlAction(
                        "askPPhra",
                        "Set first name",
                        Collections.singletonList(new YamlQuestion("kel est la phrase pattern ?")),
                        Collections.emptyList(),
                        new YamlPostProcessor("phrase", PostProcessorType.SAVE_CACHE)
                ));

        result.add(
                new YamlAction(
                        "showPhra",
                        "Set first name",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new YamlPostProcessor("firstName", PostProcessorType.SAVE_CACHE)
                ));
        return result;
    }

    public static String getRootChoiceId() {
        return "askPren";
    }

    public static void main(String[] args) {
        Launcher app = new Launcher();
        // TODO define Choice in a yaml file
        Choice askPrenom = new Choice("askPren", "Ask first name");
        Choice askPhrasePattern = new Choice("askPPhra", "Ask phrase pattern");
        Choice showPhrase = new Choice("showPhra", "show phrase");
        // TODO define the rest in the same yaml file, linked to these choices.
        // TODO define custom processor that requires extra java code and predefined processor like save in cache
        ActionSummary ASK_FIRSTNAME = new ActionSummary(
                askPrenom,
                s -> new ActionConsole(
                        askPrenom,
                        Collections.singletonList(new Question("kel est ton prenom ?")),
                        Arrays.asList(askPhrasePattern, showPhrase)),
                a -> {
                    ConsoleCache.set("prenom", a.getInputs().get(0));
                }
        );

        ActionSummary ASK_SENTENCE_PATTERN = new ActionSummary(
                askPhrasePattern,
                s -> new ActionConsole(
                        askPhrasePattern,
                        Collections.singletonList(new Question("kel est la phrase pattern ?")),
                        Collections.emptyList()
                ),
                a -> {
                    ConsoleCache.set("phrase", a.getInputs().get(0));
                }
        );
        ActionSummary SHOW_SENTENCE = new ActionSummary(
                showPhrase,
                s -> new ActionConsole(
                        showPhrase,
                        Collections.emptyList(),
                        Collections.emptyList()
                ),
                c -> {
                    Console.get().show(
                            As.string(
                                    ConsoleCache.get("phrase"))
                                    .replaceAll("\\(\\)", ConsoleCache.get("prenom")
                                    ));
                });
        List<YamlAction> yamlActions = readYaml();
        app.register(yamlActions);
        // app.register(ASK_FIRSTNAME, ASK_SENTENCE_PATTERN, SHOW_SENTENCE);
        app.manageArguments(new String[]{"-c", "console.txt"});
        app.run(ChoiceRepo.get(getRootChoiceId()));
    }

}
