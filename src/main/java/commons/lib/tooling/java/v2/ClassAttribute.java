package commons.lib.tooling.java.v2;


public class ClassAttribute {
    private final Scope scope;
    private final boolean isFinal;
    private final boolean isStatic;
    private final ObjectDeclaration object;

    public ClassAttribute(boolean isFinal, boolean isStatic, ObjectDeclaration object) {
        this.scope = Scope.PRIVATE;
        this.isFinal = isFinal;
        this.isStatic = isStatic;
        this.object = object;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public ObjectDeclaration getObject() {
        return object;
    }

    public void build(StringBuilder builder, int indentationLevel) {
        final String baseTab = new String(new char[indentationLevel]).replace("\0", "\t");
        builder.append(baseTab);
        builder.append(scope.toString().toLowerCase());
        builder.append(" ");
        if (isStatic) {
            builder.append("static ");
        }
        if (isFinal) {
            builder.append("final ");
        }
        builder.append(object.getClassDefinition().getName());
        builder.append(" ");
        builder.append(object.getName());
        builder.append(";\n");
    }
}
