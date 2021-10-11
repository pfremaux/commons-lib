package commons.lib.tooling.java.v1.code.line;

import commons.lib.tooling.java.v1.CodeBehavior;
import commons.lib.tooling.java.v1.code.CodeObject;

public class DeclareObject extends CodeBehavior {
    private final boolean isFinal;
    private final CodeObject object;

    public DeclareObject(boolean isFinal, CodeObject object) {
        this.isFinal = isFinal;
        this.object = object;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public CodeObject getObject() {
        return object;
    }

    @Override
    public void build(StringBuilder builder, int indentationLevel) {
        final String baseTab = new String(new char[indentationLevel]).replace("\0", "\t");
        builder.append(baseTab);
        if (isFinal) {
            builder.append("final ");
        }
        builder.append(object.getReferenceClass().getClassName());
        builder.append(" ");
        builder.append(object.getName());
        builder.append(";\n");
    }
}
