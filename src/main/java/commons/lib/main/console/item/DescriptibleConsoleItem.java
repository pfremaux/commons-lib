package commons.lib.main.console.item;

import commons.lib.main.console.CustomConsole;

import java.util.UUID;

public abstract class DescriptibleConsoleItem {
    private final CustomConsole console;

    private String id;
    private String humanId;
    private String name;

    public DescriptibleConsoleItem(CustomConsole console) {
        this.console = console;
        this.id = UUID.randomUUID().toString();
    }

    public abstract DescriptibleConsoleItem interactiveInit();

    public CustomConsole getConsole() {
        return console;
    }

    public String name() {
        return name;
    }
    public void name(String s) {
        this.name = s;
    }

    public void id(String id) {
        this.id = id;
    }

    public void humanId(String humanId) {
        this.humanId = humanId;
    }

    public String id() {
        return id;
    }

    public String humanId() {
        return humanId;
    }
}
