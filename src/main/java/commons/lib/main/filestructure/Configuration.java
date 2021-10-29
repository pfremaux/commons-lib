package commons.lib.main.filestructure;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class Configuration {

    private final Map<String, SubConfiguration> configData;

    public Configuration(Map<String, SubConfiguration> configData) {
        this.configData = Collections.unmodifiableMap(configData);
    }

    public SubConfiguration getSubConfiguration(String key) {
        return configData.get(key);
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "configData=" + configData.entrySet().stream().map(e -> e.getKey()+"->"+e.getValue()).collect(Collectors.joining(", "))  +
                '}';
    }
}
