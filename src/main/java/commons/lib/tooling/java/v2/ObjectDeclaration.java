package commons.lib.tooling.java.v2;

import java.util.Objects;

public class ObjectDeclaration extends CodeElement {

    private final ClassDefinition classDefinition;
    private final String name;
    private final String hardcodedValue;
    private final ObjectUsage objectUsage;

    public ObjectDeclaration(CodeElement parent, ClassDefinition classDefinition, String name) {
        super(parent);
        this.classDefinition = Objects.requireNonNull(classDefinition);
        this.name = name;
        this.hardcodedValue = null;
        this.objectUsage = new ObjectUsage(parent, name, hardcodedValue, this);
    }

    public ObjectDeclaration(CodeElement parent, ClassDefinition classDefinition, String name, String hardcodedValue) {
        super(parent);
        this.classDefinition = Objects.requireNonNull(classDefinition);
        this.name = name;
        this.hardcodedValue = hardcodedValue;
        this.objectUsage = new ObjectUsage(parent, name, hardcodedValue, this);
    }

    public ObjectDeclaration(CodeElement parent, ClassDefinition classDefinition, String name, ObjectUsage valueToAssign) {
        super(parent);
        this.classDefinition = Objects.requireNonNull(classDefinition);
        this.name = name;
        this.hardcodedValue = null;
        this.objectUsage = valueToAssign;
    }

    public ObjectUsage use() {
        return objectUsage;
    }

    protected ObjectDeclaration newInstanceWithValue(String strValue) {
        return new ObjectDeclaration(getParent(), getClassDefinition(), getName(), strValue);
    }

    @Override
    protected void build(StringBuilder b, String baseTab, int indentationLevel) {
        b.append(baseTab);
        if (name == null) {
            b.append(hardcodedValue);
        } else {
            b.append(classDefinition.getName());
            b.append(" ");
            b.append(name);
            if (hardcodedValue != null) {
                b.append(" = ");
                b.append(hardcodedValue);
            } else if (objectUsage != null) {
                b.append(" = ");
                b.append(objectUsage.getValue());
            } else {
                ObjectUsage use = use();
                if (use.isDelayed()) {
                    final MethodDeclared methodDeclared = (MethodDeclared) getParent();
                    final ObjectUsage resolve = use.resolve(methodDeclared);
                    b.append(" = ");
                    resolve.build(b, 0);
                }
            }
        }
        b.append(";\n");
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
