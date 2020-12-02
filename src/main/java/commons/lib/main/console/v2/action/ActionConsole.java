package commons.lib.main.console.v2.action;


import commons.lib.main.SystemUtils;
import commons.lib.main.console.v2.Choice;
import commons.lib.main.console.v2.Console;
import commons.lib.main.console.v2.question.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ActionConsole {
    private static final Question yesNo = new Question("");
    private final UUID uuid;
    private final List<String> inputs = new ArrayList<>(); // answers from questions
    private final List<ActionConsole> subActionsValid = new ArrayList<>();
    private final Choice choice;
    private final List<Question> questions;
    private final List<Choice> choices;
    private ActionConsole onFail = null;
    private final List<Choice> existConditions = Arrays.asList(Choice.Predefined.CANCEL_ACTION, Choice.Predefined.SAVE_ACTION);
    private boolean isValid;

    public ActionConsole(UUID uuid, Choice context, List<Question> questions, List<Choice> choices) {
        this.uuid = uuid;
        this.choice = context;
        this.questions = questions;
        this.choices = new ArrayList<>(choices);
        this.choices.addAll(existConditions);
    }

    public ActionConsole(Choice context, List<Question> questions, List<Choice> choices) {
        this(UUID.randomUUID(), context, questions, choices);
    }

    public void go() {
        if (!questions.isEmpty()) {
            askQuestions();
            if (Choice.Predefined.MAIN != choice) {
                Console.get().printf("Manage fallback ?");
                String ask = yesNo.ask();
                if (ask.startsWith("o")) {
                    onFail = ActionConsoleRepo.get(Choice.Predefined.ON_FAIL);// todo if not null always enable it
                    onFail.go();
                }
            }
        }
        managerExtraChoices();
        if (isValid) {
            postProcess();
        }
    }

    private void managerExtraChoices() {
        if (choices.isEmpty()) {
            return; // ensure this condition is never true
        }
        int response = -1;
        Choice c = null;
        while (!existConditions.contains(c)) {
            do {
                Console.get().printf("[" + getContext().getName() + "]");
                Console.get().printf("Now what ?");
                for (int i = 0; i < choices.size(); i++) {
                    Choice context = choices.get(i);
                    System.out.println(i + ". " + context.getName());
                }
                String s = Console.get().readLine();
                response = getInt(s);
            } while (response < 0 || response >= choices.size());
            c = choices.get(response);
            if (existConditions.contains(c)) {
                if (Choice.Predefined.SAVE_ACTION.getUuid().equals(c.getUuid())) {
                    isValid = true;
                }
            } else {
                ActionConsole actionConsole = ActionConsoleRepo.get(c);
                actionConsole.go();
                if (actionConsole.isValid()) {
                    subActionsValid.add(actionConsole);
                }
            }
        }
    }

    private void postProcess() {
        ActionConsoleProcessor.process(this);
        if (ActionConsoleRepo.root.getUuid().equals(this.uuid)) {
            // we're in root.
            SystemUtils.endOfApp();
        }
    }

    private int getInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return -1;
        }
    }

    private void askQuestions() {
        if (questions.isEmpty()) {
            return;
        }
        boolean accepted = false;
        do {
            for (Question question : questions) {
                String answer = question.ask();
                inputs.add(answer);
            }
            ActionConsoleProcessor.process(this);
            System.out.println("Do you confirm ?");
            String ask = yesNo.ask();
            accepted = ask.startsWith("o");
        } while (!accepted);

    }

    public List<String> getInputs() {
        return inputs;
    }

    public List<ActionConsole> getSubActionsValid() {
        return subActionsValid;
    }

    public Choice getContext() {
        return choice;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public boolean isValid() {
        return isValid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public ActionConsole getOnFail() {
        return onFail;
    }

    public void setOnFail(ActionConsole onFail) {
        this.onFail = onFail;
    }
}
