package commons.lib.console.v2.action;

import commons.lib.console.v2.Choice;

import java.util.function.Consumer;
import java.util.function.Function;

public class ActionSummary {
    private final Choice choice;
    private final Function<String, ActionConsole> action;
    private final Consumer<ActionConsole> postOperation;

    public ActionSummary(
            Choice choice,
                         Function<String, ActionConsole> action,
                         Consumer<ActionConsole> postOperation) {
        this.choice = choice;
        this.action = action;
        this.postOperation = postOperation;
    }

    public Choice getChoice() {
        return choice;
    }

    public Function<String, ActionConsole> getAction() {
        return action;
    }

    public Consumer<ActionConsole> getPostOperation() {
        return postOperation;
    }
}
