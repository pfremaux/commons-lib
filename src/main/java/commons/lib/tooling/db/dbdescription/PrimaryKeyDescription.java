package commons.lib.tooling.db.dbdescription;

public class PrimaryKeyDescription {
    private final String column;

    public PrimaryKeyDescription(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }


}
