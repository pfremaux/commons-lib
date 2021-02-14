package commons.lib.extra.gui;

import commons.lib.main.UnrecoverableException;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * The goal of this class is to help you to put swing components.
 */
public final class Positioner {
    public static final int DEFAULT_LABEL_WIDTH = 100;
    public static final int DEFAULT_BUTTON_HEIGHT = 20;
    public static final int DEFAULT_TEXTFIELD_HEIGHT = 20;
    private final static int HORIZONTAL_SPACE = 10;
    private static final int VERTICAL_SPACE = 10;
    private int currentLine = 50 + VERTICAL_SPACE;
    private Rectangle currentRect;
    private List<Component> componentList = new ArrayList<>();
    private boolean putUnder = false;
    private int maxX = 0;
    private int maxY = 0;


    public Label addLabel(String text, int width, int height) {
        final Label label = new Label(text);
        this.add(label, width, height);
        return label;
    }

    /**
     * @param text           Text displayed in the button.
     * @param width          Width in pixel.
     * @param height         Height in pixel.
     * @param actionListener Action you want to execute when the button is clicked.
     * @return An instance of a new button.
     */
    public Button addButton(String text, int width, int height, ActionListener actionListener) {
        final Button btn = new Button(text);
        btn.addActionListener(actionListener);
        this.add(btn, width, height);
        return btn;
    }

    public TextArea addTextArea(String text, int width, int height) {
        final TextArea textArea = new TextArea(text, Math.round(height / 10), Math.round(width / 10));
        textArea.setText(text);
        this.add(textArea, width, height);
        return textArea;
    }

    /**
     * @param values       Values you want to put in the list.
     * @param width        Width in pixel.
     * @param height       Height in pixel.
     * @param itemListener Action you want to perform if an element is selected in the list.
     * @return The graphical component instance.
     */
    public java.awt.List addList(String[] values, int width, int height, ItemListener itemListener) {
        final java.awt.List list = new java.awt.List();
        for (String value : values) {
            list.add(value);
        }
        list.addItemListener(itemListener);
        add(list, width, height);
        return list;
    }

    public TextField addTextField(int width, int height) {
        final TextField textField = new TextField();
        add(textField, width, height);
        return textField;
    }

    public TextField addPasswordField(int width, int height) {
        final TextField passwordField = new TextField();
        passwordField.setEchoChar('*');
        add(passwordField, width, height);
        return passwordField;
    }

    public JTree addTree(DefaultMutableTreeNode defaultMutableTreeNode, int width, int height, TreeSelectionListener treeSelectionListener) {
        final JTree tree = new JTree(defaultMutableTreeNode);
        JScrollPane jScrollPane = new JScrollPane(tree);
        tree.addTreeSelectionListener(treeSelectionListener);
        add(jScrollPane, width, height);
        return tree;
    }

    /**
     * Inform the this Positionner's instance to add the new components in a new line.
     */
    public void newLine() {
        if (currentRect == null) {
            currentLine += VERTICAL_SPACE;
        } else {
            currentLine = currentRect.y + currentRect.height + VERTICAL_SPACE;
            currentRect.x = 0;
            currentRect.y = currentLine;
            currentRect.width = 0;
        }
    }

    /**
     * Call this method when you finished to build your window.
     *
     * @param frame The frame instance on which you want to put all the components you prepared with this positioner.
     */
    public void addAllToPanel(Frame frame) {
        frame.setLayout(null);
        for (Component component : componentList) {
            frame.add(component);
        }
    }

    /**
     * Call this method when you finished to build your window.
     *
     * @param dialog The dialog instance on which you want to put all the components you prepared with this positioner.
     */
    public void addAllToDialog(Dialog dialog) {
        dialog.setLayout(null);
        for (Component component : componentList) {
            dialog.add(component);
        }
    }

    public void endCreation(Dialog dialog, String title) {
        dialog.setTitle(title);
        addAllToDialog(dialog);
        dialog.pack();
        dialog.setBounds(getWindowBound(100, 100));
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    /**
     * Search a component instance you already created.
     *
     * @param name          Component's name.
     * @param componentType The component's type.
     * @param <T>           The component's type.
     * @return The component you're searching for, or null if it doesn't exist.
     * throw UnrecoverableException Unchecked exception when the component is not found.
     */
    public <T extends Component> T getComponentByName(String name, Class<T> componentType) {
        for (Component component : componentList) {
            if (name.equals(component.getName())) {
                return componentType.cast(component);
            }
        }
        final String message = "Component " + componentType.getSimpleName() + "not found with the name " + name;
        throw new UnrecoverableException(message, message, -3);
    }

    /**
     * @return Returns <tt>true</tt> if the positioner is set up to put the next component under the previous one.
     */
    public boolean isPutUnder() {
        return putUnder;
    }

    /**
     * Set this flag to true if you want to put the next component under the previous one you created.
     *
     * @param putUnder <tt>true</tt> if the next component must be placed under the previous one.
     */
    public void setPutUnder(boolean putUnder) {
        this.putUnder = putUnder;
    }

    /**
     * Deduct the window'S size depending on the components you previously added.
     *
     * @param x The X position of the window.
     * @param y The Y position of the window.
     * @return A new rectangle instance.
     */
    public Rectangle getWindowBound(int x, int y) {
        return new Rectangle(x, y, maxX, maxY);
    }

    public static Rectangle getWindowBoundBaseOnComponent(Component c, int x, int y, int widthOffset, int heightOffset) {
        return new Rectangle(x, y, c.getWidth() + widthOffset, c.getHeight() + heightOffset);
    }

    private void addNext(Component c, int width, int height) {
        if (currentRect == null) {
            currentRect = new Rectangle(HORIZONTAL_SPACE, currentLine, width, height);
        } else {
            currentRect = new Rectangle(currentRect.x + currentRect.width + HORIZONTAL_SPACE, currentLine, width, height);
        }
        c.setBounds(currentRect);
        componentList.add(c);
    }

    private void addUnder(Component c, int width, int height) {
        if (currentRect == null) {
            currentRect = new Rectangle(HORIZONTAL_SPACE, VERTICAL_SPACE + currentLine, width, height);
        } else {
            currentRect = new Rectangle(currentRect.x, currentRect.y + currentRect.height + VERTICAL_SPACE, width, height);
        }
        c.setBounds(currentRect);
        componentList.add(c);
    }

    private void add(Component c, int width, int height) {
        if (putUnder) {
            addUnder(c, width, height);
        } else {
            addNext(c, width, height);
        }
        maxX = Math.max(maxX, currentRect.x + currentRect.width + HORIZONTAL_SPACE);
        maxY = Math.max(maxY, currentRect.y + currentRect.height + VERTICAL_SPACE);
    }
}
