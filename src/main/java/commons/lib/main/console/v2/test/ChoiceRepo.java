package commons.lib.main.console.v2.test;

import commons.lib.main.console.v2.Choice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChoiceRepo {

    private static Map<UUID, Choice> all = new HashMap<>();
    private static Map<String, Choice> allPerStrId = new HashMap<>();
    private static boolean available;

    public static void register(Choice choice) throws IllegalAccessException {
        if (available) {
            throw new IllegalAccessException("Parameters locked. Can't add more");
        }
        all.put(choice.getUuid(), choice);
        allPerStrId.put(choice.getId(), choice);
    }

    public static Choice get(UUID c) {
        return all.get(c);
    }

    public static Choice get(String c) {
        return allPerStrId.get(c);
    }

    public static Map<UUID, Choice> getAll() {
        if (available) {
            return all;
        }
        throw new RuntimeException("You have to lock InputParametersRepo first.");
    }

    public static void lock() {
        all = Collections.unmodifiableMap(all);
        available = true;
    }
}
