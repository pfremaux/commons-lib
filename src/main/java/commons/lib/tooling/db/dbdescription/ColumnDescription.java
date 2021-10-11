package commons.lib.tooling.db.dbdescription;

public class ColumnDescription {
    private final String name;
    private final Class<?> type;
    public final boolean notNull;
    private final String defaultValue;

    public ColumnDescription(String name, Class<?> type, boolean notNull, String defaultValue) {
        this.name = name;
        this.type = type;
        this.notNull = notNull;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
