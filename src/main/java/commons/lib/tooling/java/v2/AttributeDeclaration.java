package commons.lib.tooling.java.v2;

import java.util.Objects;

public class AttributeDeclaration extends CodeElement {

    private final Scope scope;
    private final boolean isStatic;
    private final boolean isFinal;
    private final ClassDefinition classDefinition;
    private final String name;
    private final String hardcodedValue;

    public AttributeDeclaration(CodeElement parent, Scope scope, boolean isStatic, boolean isFinal, ClassDefinition classDefinition, String name, String hardcodedValue) {
        super(parent);
        this.scope = scope;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
        this.classDefinition = Objects.requireNonNull(classDefinition);
        this.name = Objects.requireNonNull(name);
        this.hardcodedValue = hardcodedValue;
    }

    public ObjectUsage use() {
        return new ObjectUsage(parent, getName(), null, new ObjectDeclaration(parent, getClassDefinition(), getName()));
    }

    @Override
    protected void build(StringBuilder b, String baseTab, int indentationLevel) {
        b.append(baseTab);
        b.append(scope.code());
        b.append(" ");
        if (isStatic) {
            b.append("static ");
        }

        if (isFinal) {
            b.append("final ");
        }

        b.append(classDefinition.getName());
        b.append(" ");
        b.append(name);
        if (hardcodedValue != null) {
            b.append(" = ");
            b.append(hardcodedValue);
        }
        b.append(";\n");
    }

    public boolean isFinal() {
        return isFinal;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public ClassDefinition getClassDefinition() {
        return classDefinition;
    }

    public String getName() {
        return name;
    }

    public String getHardcodedValue() {
        return hardcodedValue;
    }
}
