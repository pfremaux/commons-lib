package commons.lib.extra.gui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class HideToSystemTray extends JFrame {
    TrayIcon trayIcon;
    SystemTray tray;

    HideToSystemTray() {
        super("SystemTray test");
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            final PopupMenu popup = setupTrayPopupMenu();
            final Image image = Toolkit.getDefaultToolkit().getImage("resources/swan.png");
            trayIcon = new TrayIcon(image, "SystemTray Demo", popup);
            trayIcon.setImageAutoSize(true);
        } else {
            System.out.println("system tray not supported");
        }
        addWindowStateListener(e -> {
            try {
                manageWindowStateEvent(e);
            } catch (AWTException ex) {
                System.out.println("unable to add to tray");
            }
        });
        setIconImage(Toolkit.getDefaultToolkit().getImage("resources/swan.png"));

        setVisible(true);
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @NotNull
    private PopupMenu setupTrayPopupMenu() {
        final PopupMenu popup = new PopupMenu();
        MenuItem defaultItem = new MenuItem("Exit");
        defaultItem.addActionListener(e -> System.exit(0));
        popup.add(defaultItem);
        defaultItem = new MenuItem("Open");
        defaultItem.addActionListener(e -> {
            setVisible(true);
            setExtendedState(JFrame.NORMAL);
        });
        popup.add(defaultItem);
        return popup;
    }

    private void manageWindowStateEvent(WindowEvent e) throws AWTException {
        switch (e.getNewState()) {
            case Frame.ICONIFIED: // Iconified means the window has been reduced, we're hiding the window and add the tray icon
            case 7:
                tray.add(trayIcon);
                setVisible(false);
                System.out.println(e.getNewState());
                break;
            case Frame.MAXIMIZED_BOTH: // If we display the window, then we're removing the icon in the system tray.
            case Frame.NORMAL:
                tray.remove(trayIcon);
                setVisible(true);
                break;
            default:
                // TODO error
        }
    }

    public static void main(String[] args) {
        new HideToSystemTray();
    }
}