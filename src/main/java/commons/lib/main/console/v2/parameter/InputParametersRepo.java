package commons.lib.main.console.v2.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InputParametersRepo {
    private static List<InputParametersContract> all = new ArrayList<>();
    private static boolean available;

    public static void register(InputParametersContract c) throws IllegalAccessException {
        if (available) {
            throw new IllegalAccessException("Parameters locked. Can't add more");
        }
        all.add(c);
    }

    public static List<InputParametersContract> getAll() {
        if (available) {
            return all;
        }
        throw new RuntimeException("You have to lock InputParametersRepo first.");
    }

    public static void lock() {
        all = Collections.unmodifiableList(all);
        available = true;
    }

}
