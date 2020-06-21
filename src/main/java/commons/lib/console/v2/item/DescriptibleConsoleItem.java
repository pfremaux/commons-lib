package commons.lib.console.v2.item;

import commons.lib.console.CustomConsole;

public abstract class DescriptibleConsoleItem {
    private final CustomConsole console;

    private String id;
    private String humanId;
    private String name;

    public DescriptibleConsoleItem(CustomConsole console) {
        this.console = console;
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
