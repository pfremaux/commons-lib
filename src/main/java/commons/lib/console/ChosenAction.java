package commons.lib.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ChosenAction implements Choosable {

    private final Map<String, List<String[]>> cache;

    public ChosenAction(Map<String, List<String[]>> cache) {
        this.cache = cache;
    }

// TODO duplicate code avec ConsoleMenu
    public List<String[]> cache(String key) {
        return cache.computeIfAbsent(key, k -> new ArrayList<>());
    }

    public List<String[]> clearCache(String key) {
        return cache.remove(key);
    }

    public Map<String, List<String[]>> getCache() {
        return cache;
    }
}
