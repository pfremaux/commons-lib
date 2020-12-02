package commons.lib;

import commons.lib.project.documentation.MdDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@MdDoc(description = "Replace strings by others")
public final class SimpleTranslator {

    private static final Logger logger = LoggerFactory.getLogger(SimpleTranslator.class);
    private final static Map<Pattern, String> rules = new HashMap<>();

    private SimpleTranslator() {
    }

    @MdDoc(description = "Loads regex patterns. Any strings that matches with it" +
            " will be replaced by a given string.")
    public static void load(
            @MdDoc(description = "The base property. ",
                    examples = {
                            "property = \"example.property\"",
                            "example.property.0.pattern=badword\nexample.property.0=****"})
                    String property) {
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

    @MdDoc(description = "Tests the given text and eventually converts it.")
    public static String translate(
            @MdDoc(description = "The string to test.") String text) {
        for (Map.Entry<Pattern, String> entry : rules.entrySet()) {
            if (entry.getKey().asMatchPredicate().test(text)) {
                return entry.getValue();
            }
        }
        return text;
    }
}
