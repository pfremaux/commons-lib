package commons.lib.project.sql2Java;

public enum ColumnType {
    INT("integer", "int", "Int"),
    BLOB("blob", "byte[]", "Bytes"),
    TEXT("text", "String", "String"),
    REAL("real", "double", "Double");

    private String typeStr;
    private String javaType;
    private final String javaSqlType;

    ColumnType(String typeStr, String javaType, String javaSqlType) {
        this.typeStr = typeStr;
        this.javaType = javaType;
        this.javaSqlType = javaSqlType;
    }

    public static ColumnType fromString(String s) {
        for (ColumnType value : values()) {
            if (value.typeStr.equalsIgnoreCase(s)) {
                return value;
            }
        }
        throw new UnsupportedOperationException(s + " is unknown");// TODO mieux
    }

    public String getTypeStr() {
        return typeStr;
    }

    public String getJavaType() {
        return javaType;
    }

    public String getJavaSqlType() {
        return javaSqlType;
    }
}
