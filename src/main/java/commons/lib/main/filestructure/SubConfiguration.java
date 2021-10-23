package commons.lib.main.filestructure;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SubConfiguration {
    private final Map<String, SubConfiguration> configData;
    private final Object value;

    public SubConfiguration(Object value) {
        this.configData = Collections.emptyMap();
        this.value = value;
    }

    public SubConfiguration(Map<String, SubConfiguration> configData) {
        this.configData = configData;
        this.value = null;
    }


    public Map<String, SubConfiguration> getConfigData() {
        return configData;
    }

    public SubConfiguration getSubConfiguration(String key) {
        return configData.get(key);
    }


    public Long getLong() {
        return (Long) value;
    }

    public List<String> getStringList() {
        return (List<String>) value;
    }

    public String get() {
        return (String) value;
    }
}
