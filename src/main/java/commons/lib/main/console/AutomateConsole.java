package commons.lib.main.console;

import commons.lib.main.SystemUtils;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutomateConsole implements CustomConsole {

    private int counter = -1;
    private List<String> answers;
    private final List<String> outputWhileDebugging = new ArrayList<>();
    private Console console;

    public AutomateConsole(Path propertyPath) {
        if (propertyPath == null) {
            answers = new ArrayList<>();
        } else {
            try {
                File file = propertyPath.toFile();
                System.out.println(file.getAbsoluteFile());
                answers = Files.readAllLines(propertyPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            answers = answers.stream().map(String::trim).collect(Collectors.toList());
        }
    }

    public AutomateConsole(List<String> answers) {
        this.answers = answers;
    }

    @Override
    public void printf(String s) {
        /*if (isDebugMode()) {
            outputWhileDebugging.add(s);
        }*/
        System.out.printf(s);
        System.out.println();
    }

    @Override
    public String readLine() {
        counter++;
        String answer;
        if (counter >= answers.size()) {
            if (this.console == null) {
                final Console console = System.console();
                if (console == null) {
                    System.out.println("No console object available. Exiting");
                    SystemUtils.failProgrammer();
                }
                this.console = console;
            }
            answer = console.readLine();
            answers.add(answer);
        } else {
            answer = answers.get(counter);
            printf(answer);
        }
        /*if (isDebugMode()) {
            outputWhileDebugging.add(answer);
        }*/
        return answer;
    }

    @Override
    public List<String> history() {
        return answers;
    }

    @Override
    public char[] readPassword() {
        char[] response = readLine().toCharArray();
/*        if (isDebugMode()) {
            outputWhileDebugging.add(new String(response));
        }*/
        return response;
    }

    @Override
    public void printf(String s, Object... objs) {
        if (isDebugMode()) {
            outputWhileDebugging.add(String.format(s, objs));
        }
        System.out.printf(s + "\n", objs);
    }

    public List<String> getOutputWhileDebugging() {
        return outputWhileDebugging;
    }
}
