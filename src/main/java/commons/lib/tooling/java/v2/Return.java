package commons.lib.tooling.java.v2;

public class Return extends CodeElement {
    private final ObjectUsage objectDeclaration;

    public Return(CodeElement parent, ObjectUsage objectUsage) {
        super(parent);
        this.objectDeclaration = objectUsage;
    }

    public ObjectUsage getObjectDeclaration() {
        return objectDeclaration;
    }

    @Override
    protected void build(StringBuilder b, String baseTab, int indentationLevel) {
        b.append(baseTab);
        b.append("return ");
        objectDeclaration.build(b, indentationLevel);
        b.append(";\n");
    }
}
