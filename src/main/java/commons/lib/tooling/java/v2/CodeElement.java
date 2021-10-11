package commons.lib.tooling.java.v2;

public abstract class CodeElement {
    protected CodeElement parent;

    public final void build(StringBuilder builder, int indentationLevel) {
        final String baseTab = new String(new char[indentationLevel]).replace("\0", "\t");
        build(builder, baseTab, indentationLevel);
    }


    protected abstract void build(StringBuilder builder, String baseTab, int indentationLevel);

    void setParent(CodeElement parent) {
        this.parent = parent;
    }

    public CodeElement(CodeElement parent) {
        this.parent = parent;
    }

    public CodeElement getParent() {
        return parent;
    }

}
