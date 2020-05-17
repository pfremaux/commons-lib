package commons.lib.console;

import java.io.Console;
import java.util.List;
import java.util.function.Function;

// TODO i18n
public class Question implements Choosable {
    private final String question;
    private final List<Option> choices;
    private final Function<String, String> processor;

    public Question(String question, List<Option> choices, Function<String, String> processor) {
        this.question = question;
        this.choices = choices;
        for (Option choice : choices) {
            if (choice.getChoosable() != null) {
                choice.getChoosable().init();
            }
        }
        this.processor = processor;
    }

    @Override
    public void init() {

    }

    @Override
    public String label() {
        return question;
    }

    @Override
    public void trigger(Console console) {
        Question.ask(console, this);
    }

    public static String ask(Console console, Question question) {
        console.printf("%s\n", question.getQuestion());
        final List<Option> choices = question.getChoices();
        for (int i = 0; i < choices.size(); i++) {
            console.printf("%d. %s\n", i, choices.get(i).getMessage());
        }
        String response;
        while (true) {
   /*         if (choices.size() == 1) {
                return processAnswer(console, question, choices.get(0));
            }*/
            response = console.readLine();
            console.flush();
            if (!choices.isEmpty()) {
                int i;
                try {
                    i = Integer.parseInt(response);
                } catch (NumberFormatException e) {
                    continue;
                }
                if (0 <= i && i < choices.size()) {
                    Option option = choices.get(i);
                    return processAnswer(console, question, option);
                }
            } else {
                break;
            }
        }
        return question.getProcessor().apply(response); // TODO string instead of parameterized
    }

    private static String processAnswer(Console console, Question question, Option option) {
        if (option.getChoosable() != null) {
            option.getChoosable().trigger(console);
            return "";
        }
        return question.getProcessor().apply(option.getValue());
    }

    public String getQuestion() {
        return question;
    }

    public Function<String, String> getProcessor() {
        return processor;
    }

    public List<Option> getChoices() {
        return choices;
    }
}
