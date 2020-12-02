package commons.lib.main.console;

import commons.lib.main.console.v2.item.DescriptibleConsoleItem;

public class MyItem extends DescriptibleConsoleItem {
    public MyItem(CustomConsole console) {
        super(console);
    }

    @Override
    public DescriptibleConsoleItem interactiveInit() {
        return new MyItem(ConsoleFactory.getInstance());
    }

    public String getValue() {
        return name();
    }

    public void setValue(String value) {
        humanId("humanId " + value);
        name(value);
    }
}
