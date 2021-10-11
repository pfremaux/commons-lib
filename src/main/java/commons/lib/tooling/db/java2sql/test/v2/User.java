package commons.lib.tooling.db.java2sql.test.v2;

import commons.lib.tooling.db.annotation.NToN;
import commons.lib.tooling.db.annotation.Table;

import java.util.List;

@Table
public class User {

    private final String name;
    private final long birthday;
    @NToN
    private final List<Permission> permissions;

    public User(String name, long birthday, List<Permission> permissions) {
        this.name = name;
        this.birthday = birthday;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public long getBirthday() {
        return birthday;
    }
}
