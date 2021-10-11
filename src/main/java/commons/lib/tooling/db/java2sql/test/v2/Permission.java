package commons.lib.tooling.db.java2sql.test.v2;

import commons.lib.tooling.db.annotation.Table;

@Table
public class Permission {
    private final int permissionLevel;

    public Permission(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

}
