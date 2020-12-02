package commons.lib.extra.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CompletableFuture;

import static commons.lib.extra.gui.Positioner.*;

public final class AskDialog extends Dialog {
    private final Label label;
    private final Button okBtn;
    private final Button cancelBtn;
    private String answer;
    private CompletableFuture<String> futureAnswer;

    public AskDialog(Dialog owner) {
        super(owner);
        final Positioner modalPositioner = new Positioner();
        this.label = modalPositioner.addLabel("<empty>", DEFAULT_LABEL_WIDTH, DEFAULT_TEXTFIELD_HEIGHT);
        modalPositioner.newLine();
        TextField answerField = modalPositioner.addTextField(500, DEFAULT_TEXTFIELD_HEIGHT);
        modalPositioner.newLine();
        this.okBtn = modalPositioner.addButton("Ok", 100, DEFAULT_BUTTON_HEIGHT, e -> {
            this.setVisible(false);
            this.answer = answerField.getText();
            futureAnswer.complete(this.answer);
        });
        this.cancelBtn = modalPositioner.addButton("Cancel", 100, DEFAULT_BUTTON_HEIGHT, e -> {
            this.setVisible(false);
            this.answer = "";
            futureAnswer.complete("");
        });
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                setVisible(false);
                answer = "";
                if (getParent() == null) {
                    System.exit(0);
                }
                futureAnswer.complete("");

            }
        });
        this.setBounds(modalPositioner.getWindowBound(200, 200));
        modalPositioner.addAllToDialog(this);
    }

    public void ask(String message) {
        this.futureAnswer = new CompletableFuture<>();
        label.setText(message);
        label.setSize(message.length() * 7, 30);
        setVisible(true);
    }

    public void ask(String message, CompletableFuture<String> waitingAnswer) {
        this.futureAnswer = waitingAnswer;
        label.setText(message);
        label.setSize(message.length() * 7, 30);
        this.setBounds(Positioner.getWindowBoundBaseOnComponent(label, getX(), getY(), 0, 100));
        setVisible(true);
    }

    public String getAnswer() {
        return answer;
    }
}
