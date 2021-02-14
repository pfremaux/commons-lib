package commons.lib.main.console;

import commons.lib.main.SystemUtils;
import commons.lib.main.console.v3.init.CliApp;

public class CliAppExample extends CliApp {

    public static final String W = "-w";
    public static final String Q = "-q";

    private String welcomeMessage;
    private String question;
    private CustomConsole console;

    public void init() {
        register(W, "welcome.message", "Hello !", "The welcome message displayed first.");
        register(Q, "question", "What's your name ?", "The question you want to ask first.");
        console = ConsoleFactory.getInstance();
    }

    public static void main(String[] args) {
        CliAppExample cliAppExample = new CliAppExample();
        cliAppExample.validateAndLoad(args);
        cliAppExample.processParameters();
    }

    public void processParameters() {
        welcomeMessage = getValueWithCommandLine(W);
        if (welcomeMessage == null) {
            console.printf("missing parameter " + W);
            SystemUtils.failSystem();
        }

        question = getValueWithCommandLine(Q);
        if (question == null) {
            console.printf("missing parameter " + Q);
            SystemUtils.failSystem();
        }

        console.printf(welcomeMessage);
        console.printf(question);
    }

}
