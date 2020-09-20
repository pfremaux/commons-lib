package commons.lib.console;

import commons.lib.console.v2.item.DescriptibleConsoleItem;

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
