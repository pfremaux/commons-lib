package commons.lib.main.console;

import commons.lib.extra.gui.Positioner;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiConsole implements CustomConsole {

    private int counter = -1;
    private List<String> answers = new ArrayList<>();
    private final List<String> outputWhileDebugging = new ArrayList<>();

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
        Dialog dialog = new Dialog((Frame) null);
        Positioner positioner = new Positioner();
        TextField textField = positioner.addTextField(300, 30);
        List<String> result = new ArrayList<>();
        positioner.addButton("Submit", 300, 30, e -> {
            result.add(textField.getText());
            dialog.setVisible(false);
        });
        positioner.endCreation(dialog, "ti");
        dialog.setModal(true);
        dialog.setVisible(true);
        String s = result.get(0);
        answers.add(s);
        return s;
    }

    @Override
    public List<String> history() {
        return answers;
    }

    @Override
    public char[] readPassword() {
        Dialog dialog = new Dialog((Frame) null);
        Positioner positioner = new Positioner();
        TextField textField = positioner.addPasswordField(300, 30);
        List<String> result = new ArrayList<>();
        positioner.addButton("Submit", 300, 30, e -> {
            result.add(textField.getText());
            dialog.setVisible(false);
        });
        positioner.endCreation(dialog, "ti");
        dialog.setModal(true);
        dialog.setVisible(true);
        return result.get(0).toCharArray();
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
