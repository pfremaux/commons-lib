package commons.lib.main.console;

import commons.lib.tooling.documentation.MdDoc;

import java.util.Objects;

@MdDoc(description = "Utility class you can use to cast an object to a specific type. It won't trigger warnings in your source files.")
public class As {

    private As() {
    }

    @MdDoc(description = "Cast an object to a string.")
    public static String string(Object o) {
        return Objects.toString(o);
    }

    @MdDoc(description = "Cast an object to an integer.")
    public static Integer integer(Object o) {
        if (o instanceof Integer) {
            return (Integer) o;
        }
        return Integer.valueOf(string(o));
    }

    @SuppressWarnings(value = "unchecked")
    public static <T> T any(Object o) {
        return (T) o;
    }
}
