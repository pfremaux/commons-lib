package commons.lib.main.console;

import java.util.Objects;

public class As {

    public static String string(Object o) {
        return Objects.toString(o);
    }

    public static Integer integer(Object o) {
        if (o instanceof Integer) {
            return (Integer) o;
        }
        return Integer.valueOf(string(o));
    }
}
