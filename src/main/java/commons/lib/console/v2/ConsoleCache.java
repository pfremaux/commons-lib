package commons.lib.console.v2;

import java.util.HashMap;
import java.util.Map;

public class ConsoleCache {
    private static final Map<Class<?>, Object> cache = new HashMap<>();
    private static final Map<String, Object> cachePerKey = new HashMap<>();

    public static <T> T set(T value, Class<?> classz) {
        return (T) cache.put(classz, value);
    }

    public static <T> T set(T value) {
        return (T) cache.put(value.getClass(), value);
    }

    public static <T> T get(Class<T> aClass) {
        return (T) cache.get(aClass);
    }

    public static <T> T set(String key, T value) {
        return (T) cachePerKey.put(key, value);
    }

    public static <T> T get(String key) {
        return (T) cachePerKey.get(key);
    }
}
