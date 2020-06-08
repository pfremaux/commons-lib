package commons.lib.console;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutomateConsole implements CustomConsole {

    private int counter = -1;
    private List<String> answers;
    private Console console;

    public AutomateConsole(Path propertyPath) {
        if (propertyPath == null) {
            answers = new ArrayList<>();
        } else {
            try {
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
    public void show(String s) {
        System.out.println(s);
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
                    System.exit(-1);
                }
                this.console = console;
            }
            answer = console.readLine();
            answers.add(answer);
        } else {
            answer = answers.get(counter);
            show(answer);
        }
        return answer;
    }

    @Override
    public List<String> history() {
        return answers;
    }
}
