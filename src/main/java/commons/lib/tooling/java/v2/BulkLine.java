package commons.lib.tooling.java.v2;

public class BulkLine extends CodeElement{
    private final String value;
    public BulkLine(CodeElement parent, String value) {
        super(parent);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    protected void build(StringBuilder b, String baseTab, int indentationLevel) {
        b.append(baseTab);
        b.append(value);
    }
}
