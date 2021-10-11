package commons.lib.tooling.java.v2;

import java.util.ArrayList;
import java.util.List;

public class ClassDefinition extends ClassBlock {


    private final String packagePath;
    private final String name;

    public ClassDefinition(CodeElement parent, String packagePath, String name) {
        super(parent);
        this.packagePath = packagePath;
        this.name = name;
    }

    public ClassDefinition newInstanceWithParent(CodeElement parent) {
        return new ClassDefinition(parent, packagePath, name);
    }

    public String getName() {
        return name;
    }

    public String getPackagePath() {
        return packagePath;
    }

    @Override
    protected void build(StringBuilder b, String baseTab, int indentationLevel) {
        b.append("package ");
        b.append(packagePath);
        b.append(";\n");

        for (ClassDefinition c : getClassDirectlyReferenced().values()) {
            b.append("import ");
            b.append(c.getPackagePath());
            b.append(".");
            b.append(c.getName());
            b.append(";\n");
        }

        b.append("public class ");
        b.append(getName());
        b.append(" {\n");
        final List<AttributeDeclaration> mandatoryInitialization = new ArrayList<>();
        for (CodeElement blockLine : getBlockLines().values()) {
            if (blockLine instanceof AttributeDeclaration) {
                mandatoryInitialization.add((AttributeDeclaration) blockLine);
            }
        }
        final MethodDeclared constructorDeclared = MethodDeclared.getConstructorInstance(this, Scope.PUBLIC, this, mandatoryInitialization);
        getConstructors().add(constructorDeclared);

        for (CodeElement blockLine : getBlockLines().values()) {
            blockLine.build(b, indentationLevel + 1);
        }
        for (MethodDeclared constructor : getConstructors()) {
            constructor.build(b, indentationLevel + 1);
        }
        for (CodeElement blockMethod : getBlockMethods().values()) {
            blockMethod.build(b, indentationLevel + 1);
        }

        b.append("}\n");

    }
}
