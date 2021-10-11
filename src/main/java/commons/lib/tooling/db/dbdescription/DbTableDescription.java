package commons.lib.tooling.db.dbdescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DbTableDescription {
    private String tableName;
    private final Map<String, ColumnDescription> columns = new HashMap<>();
    private PrimaryKeyDescription primaryKeyDescription;
    private final List<ForeignKeyDescription> foreignKeys = new ArrayList<>();


    public void setTableName(String tableName) {
        this.tableName = tableName;
        this.primaryKeyDescription = new PrimaryKeyDescription(tableName + "_id");
    }

    public String getTableName() {
        return tableName;
    }

    public void addColumn(ColumnDescription columnDescription) {
        this.columns.put(columnDescription.getName(), columnDescription);
    }

    public PrimaryKeyDescription getPrimaryKeyDescription() {
        return primaryKeyDescription;
    }

    public List<ForeignKeyDescription> getForeignKeys() {
        return foreignKeys;
    }

    public void addFK(String foreignTable) {
        this.foreignKeys.add(new ForeignKeyDescription(foreignTable+ "_id", foreignTable, "id_"+foreignTable));
    }


    public Map<String, ColumnDescription> getColumns() {
        return columns;
    }


    public String toPrettyString() {
        return tableName + "( " + columns.entrySet().stream().map(entry -> entry.getKey() + '(' + entry.getValue() + ')').collect(Collectors.joining(", "))
                + ")";
    }

    @Override
    public String toString() {
        return "DbTableDescription{" +
                "tableName='" + tableName + '\'' +
                ", columns=" + columns +
                ", primaryKeyDescription=" + primaryKeyDescription +
                ", foreignKeys=" + foreignKeys +
                '}';
    }
}
