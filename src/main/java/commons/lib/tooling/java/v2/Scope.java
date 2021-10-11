package commons.lib.tooling.java.v2;

enum Scope {
    PUBLIC, PRIVATE, PROTECTED, PACKAGE;
    public String code() {
        if (this == PACKAGE) {
            return "";
        }
        return toString().toLowerCase();
    }
}
