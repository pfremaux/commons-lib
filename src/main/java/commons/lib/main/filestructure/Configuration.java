package commons.lib.main.filestructure;

import java.util.Collections;
import java.util.Map;

public class Configuration {

    private final Map<String, SubConfiguration> configData;

    public Configuration(Map<String, SubConfiguration> configData) {
        this.configData = Collections.unmodifiableMap(configData);
    }


    public SubConfiguration getSubConfiguration(String key) {
        return configData.get(key);
    }
}
