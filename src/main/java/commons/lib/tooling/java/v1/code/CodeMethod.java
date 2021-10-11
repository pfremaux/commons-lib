package commons.lib.tooling.java.v1.code;

import commons.lib.tooling.java.v1.CodeBehavior;
import commons.lib.tooling.java.v1.code.line.ConstructorParameterAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CodeMethod {

    private final boolean isStatic;
    private final Scope scope;
    private final String name;
    private final ReferenceClass returnType;
    private final List<CodeObject> parameters;
    private final List<CodeBehavior> codeBehaviors;
    // For generating imports in its class
    private final List<ReferenceClass> typesReferenced;
    private boolean isConstructor;

    public static CodeMethod getConstructorInstance(Scope scope, ReferenceClass cla, List<ClassAttribute> parameters) {
        final List<CodeBehavior> lines = new ArrayList<>();
        parameters.forEach(classAttribute -> lines.add(new ConstructorParameterAssignment(classAttribute.getObject())));
        CodeMethod codeMethod = new CodeMethod(
                scope,
                cla.getClassName(),
                null,
                parameters.stream().map(ClassAttribute::getObject).collect(Collectors.toList()),
                lines,
                List.of());
        codeMethod.isConstructor = true;
        return codeMethod;
    }

    public CodeMethod(boolean isStatic, Scope scope, String name, ReferenceClass returnType, List<CodeObject> parameters, List<CodeBehavior> codeBehaviors, List<ReferenceClass> typesReferenced) {
        this.isStatic = isStatic;
        this.scope = scope;
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.codeBehaviors = codeBehaviors;
        this.typesReferenced = typesReferenced;
    }

    public CodeMethod(Scope scope, String name, ReferenceClass returnType, List<CodeObject> parameters, List<CodeBehavior> codeBehaviors, List<ReferenceClass> typesReferenced) {
        this.isStatic = false;
        this.scope = scope;
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.codeBehaviors = codeBehaviors;
        this.typesReferenced = typesReferenced;
    }

    public List<CodeObject> getParameters() {
        return parameters;
    }

    public Scope getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public ReferenceClass getReturnType() {
        return returnType;
    }

    public List<CodeBehavior> getCodeBehaviors() {
        return codeBehaviors;
    }

    public List<ReferenceClass> getTypesReferenced() {
        return typesReferenced;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public void build(StringBuilder builder, int indentationLevel) {
        final String baseTab = new String(new char[indentationLevel]).replace("\0", "\t");
        builder.append(baseTab);

        if (this.scope == null) {
            builder.append("public");
        } else {
            builder.append(scope.toString().toLowerCase());
        }

        if (!isConstructor) {
            builder.append(" ");
            if (returnType == null) {
                builder.append("void");
            } else {
                builder.append(returnType.getClassName());
            }
        }

        builder.append(" ");
        builder.append(name);
        builder.append("(");
        final StringBuilder parametersBuilder = new StringBuilder();
        parameters.forEach(codeObject -> {
            parametersBuilder.append(codeObject.getReferenceClass().getClassName());
            parametersBuilder.append(" ");
            parametersBuilder.append(codeObject.getName());
            parametersBuilder.append(",");
        });
        if (parametersBuilder.length() > 0) {
            parametersBuilder.deleteCharAt(parametersBuilder.length() - 1);
        }
        builder.append(parametersBuilder);
        builder.append(") {\n");

        for (CodeBehavior codeBehavior : codeBehaviors) {
            codeBehavior.build(builder, indentationLevel + 1);
        }
        builder.append(baseTab);
        builder.append("}\n");
    }
}

