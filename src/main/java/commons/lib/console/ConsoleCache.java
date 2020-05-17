package commons.lib.console;

import java.util.HashMap;
import java.util.Map;

public class ConsoleCache {
    private static final Map<Class<?>, Object> cache = new HashMap<>();

    public static <T> T set(T value, Class<?> classz) {
        return (T) cache.put(classz, value);
    }

    public static <T> T set(T value) {
        return (T) cache.put(value.getClass(), value);
    }

    public static <T> T get(Class<T> aClass) {
        return (T) cache.get(aClass);
    }
}
