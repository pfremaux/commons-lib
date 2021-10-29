package commons.lib.main.filestructure;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.Collectors;

public class SubConfiguration {
    private final TreeMap<String, SubConfiguration> configData;
    private final Object value;
    private final  SubConfiguration parent;

    public SubConfiguration(SubConfiguration parent, Object value) {
        this.parent = parent;
        this.configData = new TreeMap<>();
        this.value = value;
    }

    public SubConfiguration(SubConfiguration parent, Map<String, SubConfiguration> configData) {
        this.parent = parent;
        this.configData = new TreeMap<>(configData);
        this.value = null;
    }


    public Map<String, SubConfiguration> getConfigData() {
        return configData;
    }

    public SortedMap<String, SubConfiguration> findAllStartingWith(String startingWith) {
        return configData.subMap(startingWith, startingWith+"z");
    }

    public SubConfiguration getSubConfiguration(String key) {
        return configData.get(key);
    }

    public Long getLong() {
        return (Long) value;
    }
    public Double getDouble() {
        return (Double) value;
    }
    public Boolean getBoolean() {
        return (Boolean) value;
    }

    public List<String> getStringList() {
        return (List<String>) value;
    }
    public List<Long> getLongList() {
        return (List<Long>) value;
    }

    public StringBuilder getCurrentBuilder() {
        return (StringBuilder)value;
    }

    public boolean hasValue() {
        return value != null;
    }

    public String get() {
        return (String) value;
    }

    public String smartGet() {
        if (value instanceof String) {
           return get();
        } else if (value instanceof Long) {
            return Long.toString(getLong());
        } else if (value instanceof Double) {
            return value.toString();// TODO
        }  else if (value instanceof Boolean) {
            return getBoolean().toString();
        } else if (value instanceof TemporalAccessor) {
            final TemporalAccessor temporalAccessor = (TemporalAccessor) value;
            LocalDate query = temporalAccessor.query(TemporalQueries.localDate());
            LocalTime query1 = temporalAccessor.query(TemporalQueries.localTime());
            return  query.toString() + " " + query1.toString();
        } else if (value instanceof ArrayList) {
            ArrayList<?> lst = (ArrayList<?>) value;
            if (lst.get(0) instanceof Long) {
                return getLongList().toString();
            } else if (lst.get(0) instanceof String) {
                return getStringList().toString();
            }
        } else if (value instanceof Collection) {
        return getStringList().toString();
    }
        return null;
    }


    public SubConfiguration getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "SubConfiguration{" +
                "configData=" + configData.entrySet().stream().map(e -> e.getKey()+"->"+e.getValue()).collect(Collectors.joining(", ")) +
                ", value=" + value +
                '}';
    }
}
