package commons.lib.tooling.db.dbscript;

import commons.lib.tooling.db.dbdescription.ColumnDescription;
import commons.lib.tooling.db.dbdescription.DbTableDescription;
import commons.lib.tooling.db.dbdescription.ForeignKeyDescription;

import java.util.List;
import java.util.Map;

public class DbScriptCreator {
    public static StringBuilder generateSql(List<DbTableDescription> tablesDescriptions) {
        final StringBuilder builder = new StringBuilder();
        for (DbTableDescription tableDescription : tablesDescriptions) {
            String tableName = tableDescription.getTableName();
            builder.append("\nCREATE TABLE ");
            builder.append(tableName);
            builder.append(" ( ");

            for (Map.Entry<String, ColumnDescription> entry : tableDescription.getColumns().entrySet()) {
                declareSqlColumn(builder, entry.getKey(), javaTypeToSqlType(entry.getValue().getType()));
            }
            declarePrimaryKey(builder, tableDescription);
            declareForeignKeys(builder, tableDescription);
            builder.deleteCharAt(builder.lastIndexOf(","));
            builder.append(" ); ");
        }

        return builder;
    }

    private static void declareSqlColumn(StringBuilder builder, String columnName, String columnType) {
        builder.append(columnName);
        builder.append(" ");
        builder.append(columnType);
        builder.append(",");
    }

    private static void declarePrimaryKey(StringBuilder builder, DbTableDescription tableDescription) {
        builder.append(" PRIMARY KEY (");
        builder.append(tableDescription.getPrimaryKeyDescription().getColumn());
        builder.append("),");
    }

    private static void declareForeignKeys(StringBuilder builder, DbTableDescription tableDescription) {
        for (ForeignKeyDescription foreignKey : tableDescription.getForeignKeys()) {
            builder.append(" FOREIGN KEY (");
            builder.append(foreignKey.getColumn());
            builder.append(") ");
            builder.append("REFERENCES ");
            builder.append(foreignKey.getReferenceTable());
            builder.append(" (");
            builder.append(foreignKey.getReferenceColumn());
            builder.append("),");
        }
    }

    private static String javaTypeToSqlType(Class<?> type) {
        if (int.class.equals(type) || Integer.class.equals(type) || Long.class.equals(type)) {
            return "int";
        } else if ("String".equals(type.getSimpleName())) {
            return "varchar(255)";
        }
        System.out.println("Unrecognized : " + type.getSimpleName());
        return "?";
    }
}
