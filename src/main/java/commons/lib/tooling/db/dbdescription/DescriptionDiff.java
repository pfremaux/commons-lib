package commons.lib.tooling.db.dbdescription;

import java.util.ArrayList;
import java.util.List;

public class DescriptionDiff {

    private final List<ColumnDescription> addedColumn = new ArrayList<>();
    private final List<ColumnDescription> updatedColumn = new ArrayList<>();
    private final List<String> removedColumn = new ArrayList<>();


    public List<ColumnDescription> getAddedColumn() {
        return addedColumn;
    }

    public List<ColumnDescription> getUpdatedColumn() {
        return updatedColumn;
    }

    public List<String> getRemovedColumn() {
        return removedColumn;
    }
}
