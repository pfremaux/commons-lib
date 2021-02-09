package commons.lib.functionaltests;

import commons.lib.functionaltests.settings.FunctionalTestsSettings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * For enabling assertions, add parameter -ea
 */
public class Runner {

    static {
        final Properties properties = System.getProperties();
        for (String[] defaultProperty : FunctionalTestsSettings.DEFAULT_PROPERTIES) {
            String key = defaultProperty[0];
            String property = properties.getProperty(key);
            if (property == null) {
                System.setProperty(key, defaultProperty[1]);
            }
        }
        System.setProperty("mode.debug", "true");
    }

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        for (Class<?> registeredTest : FunctionalTestsSettings.REGISTERED_TESTS) {
            final Method method = registeredTest.getMethod("main", String[].class);
            final String[] params = null;
            method.invoke(null, (Object) params);
        }
    }

}
