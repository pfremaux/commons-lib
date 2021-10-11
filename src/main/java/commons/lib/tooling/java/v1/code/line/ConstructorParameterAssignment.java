package commons.lib.tooling.java.v1.code.line;

import commons.lib.tooling.java.v1.CodeBehavior;
import commons.lib.tooling.java.v1.code.CodeObject;

public class ConstructorParameterAssignment extends CodeBehavior {
    private final CodeObject object;

    public ConstructorParameterAssignment(CodeObject object) {
        this.object = object;
    }

    public CodeObject getObject() {
        return object;
    }

    @Override
    public void build(StringBuilder builder, int indentationLevel) {
        final String baseTab = new String(new char[indentationLevel]).replace("\0", "\t");
        builder.append(baseTab);
        builder.append("this.");
        builder.append(object.getName());
        builder.append(" = ");
        builder.append(object.getName());
        builder.append(";\n");

    }
}