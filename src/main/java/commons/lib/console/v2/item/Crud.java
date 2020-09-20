package commons.lib.console.v2.item;

import commons.lib.console.CustomConsole;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


public class Crud<T extends DescriptibleConsoleItem> {
    public static final int PAGE_SIZE = 10;
    private final CustomConsole console;
    private final List<T> list;

    public Crud(CustomConsole console, List<T> list) {
        this.console = console;
        this.list = list;
    }

    public T interact(T defaultvalue) {
        return interact((CustomConsole c) -> defaultvalue);
    }

    public T interact(Function<CustomConsole, ? extends T> getInstance) {
        T newInstance = getInstance.apply(console);
        boolean notExistingElement = list.isEmpty();
        T result = null;
        int i = 1;
        if (!notExistingElement) {
            console.printf(String.format("1. New %s", newInstance.getClass().getSimpleName()));
            console.printf("2. Existing one");
            i = Integer.parseInt(console.readLine());
        }
        if (i == 1) {
            newInstance.interactiveInit();
            return newInstance;
        } else if (i == 2) {
            boolean ended = false;
            int pageNum = 0;
            while (!ended) {
                final List<String> page = list(pageNum);
                int counter = 0;
                for (String s : page) {
                    console.printf("%d. %s", counter, s);
                    counter++;
                }
                console.printf("n : next");
                console.printf("p : previous");
                final String s = console.readLine();
                if (Character.isDigit(s.charAt(0))) {
                    ended = true;
                    result = get(Integer.parseInt(s), pageNum);
                } else if ("q".equals(s)) {
                    ended = true;
                } else if ("n".equals(s)) {
                    pageNum = Math.min(pageNum + 1, list.size() / PAGE_SIZE);
                } else if ("p".equals(s)) {
                    pageNum = Math.min(pageNum - 1, 0);
                }
            }
        }
        return result;
    }

    public List<String> list(int page) {
        final List<String> result = new ArrayList<>();
        for (int i = 0; i < PAGE_SIZE && i < list.size(); i++) {
            final String name = list.get(i + page * PAGE_SIZE).name();
            result.add(/*i + ". " + */name);
        }
        return result;
    }

    public Optional<T> getByHumanId(String id) {
        for (T elem : list) {
            if (id.equalsIgnoreCase(elem.humanId())) {
                return Optional.of(elem);
            }
        }
        return Optional.empty();
    }

    public T get(int i, int page) {
        return list.get(i + page * PAGE_SIZE);
    }

    public T delete(int i, int page) {
        return list.remove(i + page * PAGE_SIZE);
    }

    public void insert(int i, int page, T c) {
        list.add(i + page * PAGE_SIZE, c);
    }

    public void add(T c) {
        list.add(c);
    }
}
