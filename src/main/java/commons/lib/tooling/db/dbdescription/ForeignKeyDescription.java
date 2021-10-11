package commons.lib.tooling.db.dbdescription;

public class ForeignKeyDescription {
    private final String column;
    private final String referenceColumn;
    private final String referenceTable;

    public ForeignKeyDescription(String column, String referenceTable, String referenceColumn) {
        this.column = column;
        this.referenceColumn = referenceColumn;
        this.referenceTable = referenceTable;
    }

    public String getColumn() {
        return column;
    }

    public String getReferenceColumn() {
        return referenceColumn;
    }

    public String getReferenceTable() {
        return referenceTable;
    }


}
