package commons.lib.tooling.java.v2;

public class TryBlock extends Block {

    private final CodeElement closeableElement;

    public TryBlock(CodeElement parent) {
        super(parent);
        this.closeableElement = null;
    }

    public TryBlock(CodeElement parent, CodeElement closeableElement) {
        super(parent);
        this.closeableElement = closeableElement;
    }

    public CodeElement getCloseableElement() {
        return closeableElement;
    }

    @Override
    protected void build(StringBuilder b, String baseTab, int indentationLevel) {
        b.append(baseTab);
        if (closeableElement != null) {
            b.append("try (");
            StringBuilder enclosedBuilder = new StringBuilder();
            closeableElement.build(enclosedBuilder, 0);
            b.append( enclosedBuilder.toString().replaceAll(";", "").trim());
            b.append(") {\n");
        } else {
            b.append("try {\n");
        }
        getBlockLines().forEach(line -> line.build(b, indentationLevel + 1));
        b.append(baseTab);
        b.append("}\n");
    }

    @Override
    public Block end() {
        return (Block) this.getParent();
    }
}
