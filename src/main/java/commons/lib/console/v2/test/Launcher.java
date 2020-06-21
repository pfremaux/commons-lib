package commons.lib.console.v2.test;

import commons.lib.console.As;
import commons.lib.console.v2.AbstractLauncher;
import commons.lib.console.v2.Choice;
import commons.lib.console.v2.Console;
import commons.lib.console.v2.ConsoleCache;
import commons.lib.console.v2.action.ActionConsole;
import commons.lib.console.v2.action.ActionSummary;
import commons.lib.console.v2.question.Question;

import java.util.Arrays;
import java.util.Collections;

public class Launcher extends AbstractLauncher {

    public static void main(String[] args) {
        Launcher app = new Launcher();
        Choice askPrenom = new Choice("askPren", "Ask first name");
        Choice askPhrasePattern = new Choice("askPPhra", "Ask phrase pattern");
        Choice showPhrase = new Choice("showPhra", "show phrase");
        ActionSummary ASK_FIRSTNAME = new ActionSummary(askPrenom,
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
        app.register(ASK_FIRSTNAME, ASK_SENTENCE_PATTERN, SHOW_SENTENCE);
        app.manageArguments(new String[]{"-c", "console.txt"});
        app.run(askPrenom);
    }

}
