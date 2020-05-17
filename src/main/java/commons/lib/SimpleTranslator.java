package commons.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class SimpleTranslator {

    private static final Logger logger = LoggerFactory.getLogger(SimpleTranslator.class);
    private final static Map<Pattern, String> rules = new HashMap<>();

    private SimpleTranslator() {
    }

    public static void load(String property) {
        String strPattern;
        int i = 0;
        while ((strPattern = System.getProperty(property + "." + i + ".pattern")) != null) {
            final Pattern key = Pattern.compile(strPattern);
            final String value = System.getProperty(property + "." + i);
            final String erasedValue = rules.put(key, value);
            if (erasedValue != null) {
                logger.warn("Duplicate key {}.", key);
            }
            i++;
        }
    }

    public static String translate(String text) {
        for (Map.Entry<Pattern, String> entry : rules.entrySet()) {
            if (entry.getKey().asMatchPredicate().test(text)) {
                return entry.getValue();
            }
        }
        return text;
    }
}
