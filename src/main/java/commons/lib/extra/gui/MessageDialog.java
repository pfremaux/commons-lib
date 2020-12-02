package commons.lib.extra.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static commons.lib.extra.gui.Positioner.DEFAULT_BUTTON_HEIGHT;
import static commons.lib.extra.gui.Positioner.DEFAULT_TEXTFIELD_HEIGHT;

public final class MessageDialog extends Dialog {
    private final Label label;
    private final Button okBtn;

    public MessageDialog(Dialog owner, String[] messages) {
        super(owner);
        setModal(true);
        final Positioner modalPositioner = new Positioner();
        StringBuilder stringBuilder = new StringBuilder();
        for (String msg : messages) {
            stringBuilder.append(msg);
            stringBuilder.append("\n");
        }
        final String message = stringBuilder.toString();
        this.label = modalPositioner.addLabel(message, message.length() * 10, DEFAULT_TEXTFIELD_HEIGHT);
        modalPositioner.newLine();
        this.okBtn = modalPositioner.addButton("Ok", 100, DEFAULT_BUTTON_HEIGHT, e -> this.setVisible(false));
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                setVisible(false);
                if (getParent() == null) {
                    System.exit(0);
                }

            }
        });
        this.setBounds(modalPositioner.getWindowBound(200, 200));
        modalPositioner.addAllToDialog(this);
    }

}
