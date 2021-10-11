package commons.lib.tooling.java.v1.code.line;

import commons.lib.tooling.java.v1.CodeBehavior;
import commons.lib.tooling.java.v1.code.CodeObject;

public class Return extends CodeBehavior {
    private final CodeObject objectReturned;

        public Return(CodeObject objectReturned) {
        this.objectReturned = objectReturned;
    }

    public CodeObject getObjectReturned() {
        return objectReturned;
    }

    @Override
    public void build(StringBuilder builder, int indentationLevel) {
        final String baseTab = new String(new char[indentationLevel]).replace("\0", "\t");
        builder.append(baseTab);
        builder.append("return ");
        objectReturned.build(builder);
        builder.append(";\n");

    }
}