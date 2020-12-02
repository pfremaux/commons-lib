package commons.lib.main.console;


import commons.lib.main.console.v2.item.Crud;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class TestConsoleCrud {

    @Test
    public void testCrud() {
        int chosen = 3;
        Map<CustomConsole, List<MyItem>> customConsoleListMap = prepareData(10, chosen);
        CustomConsole console = null;
        List<MyItem> items = null;
        for (Map.Entry<CustomConsole, List<MyItem>> entry : customConsoleListMap.entrySet()) {
            console = entry.getKey();
            items = entry.getValue();
            break;
        }
        Crud<MyItem> item = new Crud<>(console, items);
        MyItem defaultvalue = new MyItem(console);
        defaultvalue.setValue("-1111");
        MyItem interact = item.interact(defaultvalue);
        Assert.assertEquals(Integer.toString(chosen), interact.getValue());
    }

    public void testPagination() {
        int pageSize = Crud.PAGE_SIZE;
        Map<CustomConsole, List<MyItem>> customConsoleListMap = prepareData(pageSize * 3, 0);
    }

    private Map<CustomConsole, List<MyItem>> prepareData(int numberOfItems, int chosen) {
        final int maxItemNumber = numberOfItems;
        final List<String> answers = Arrays.asList(
                "2",
                Integer.toString(chosen)
        );

        final CustomConsole console = ConsoleFactory.getInstance(answers);
        final List<MyItem> items = new ArrayList<>();
        for (int i = 0; i < maxItemNumber; i++) {
            MyItem item = new MyItem(console);
            item.setValue(Integer.toString(i));
            items.add(item);
        }
        return Collections.singletonMap(console, items);
    }

}
