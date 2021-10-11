package commons.lib.tooling.java.v2;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MethodDeclared extends Block {

    private final boolean isStatic;
    private final Scope scope;
    private final String name;
    private final List<ObjectDeclaration> parameters;
    private final ClassDefinition returnType;
    // For generating imports in its class
    private final List<ClassDefinition> typesReferenced;
    private boolean isConstructor;

    public static MethodDeclared getConstructorInstance(CodeElement parent, Scope scope, ClassDefinition cla, List<AttributeDeclaration> parameters) {
        final List<ObjectDeclaration> objectDeclarations = parameters.stream().map(a -> new ObjectDeclaration(parent, a.getClassDefinition(), a.getName(), (String) null)).collect(Collectors.toList());
        MethodDeclared codeMethod = new MethodDeclared(
                parent,
                scope,
                cla.getName(),
                objectDeclarations,
                List.of());
        codeMethod.isConstructor = true;
        for (ObjectDeclaration parameter : objectDeclarations) {
            codeMethod.assign(parameter.getName(), parameter.use());
        }
        return codeMethod;
    }

    public MethodDeclared(CodeElement parent, boolean isStatic, Scope scope, ClassDefinition returnType, String name, List<ObjectDeclaration> parameters, List<ClassDefinition> typesReferenced) {
        super(parent);
        this.isStatic = isStatic;
        this.scope = scope;
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.typesReferenced = typesReferenced;
        for (ObjectDeclaration parameter : parameters) {
            this.getObjectDirectlyReferenced().put(parameter.getName(), parameter);
        }
    }


    public MethodDeclared(CodeElement parent, Scope scope, String name, List<ObjectDeclaration> parameters, List<ClassDefinition> typesReferenced) {
        this(parent, false, scope, null, name, parameters, typesReferenced);
    }

    @Override
    public ClassBlock end() {
        return (ClassBlock) parent;
    }

    public List<ObjectDeclaration> getParameters() {
        return parameters;
    }

    public Scope getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }


    public List<ClassDefinition> getTypesReferenced() {
        return typesReferenced;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isConstructor() {
        return isConstructor;
    }


    @Override
    protected void build(StringBuilder b, String baseTab, int indentationLevel) {
        List<Return> allReturnLines = getAllReturnLines(getBlockLines());
        ClassDefinition returnTypeDeducted = null;
        // TODO PFR make it smarter
        if (!allReturnLines.isEmpty()) {
            returnTypeDeducted = allReturnLines.get(0).getObjectDeclaration().getOrigin().getClassDefinition();
        }

        b.append(baseTab);
        b.append(getScope().toString().toLowerCase());
        b.append(" ");
        if (isStatic) {
            b.append("static ");
        }
        if (!isConstructor) {
            if (returnTypeDeducted == null) {
                b.append("void ");
            } else {
                b.append(returnTypeDeducted.getName());
                b.append(" ");
            }
        }

        b.append(getName());
        b.append("(");
        final StringBuilder parametersBuilder = new StringBuilder();
        for (ObjectDeclaration parameter : parameters) {
            parametersBuilder.append(parameter.getClassDefinition().getName());
            parametersBuilder.append(" ");
            parametersBuilder.append(parameter.getName());
            parametersBuilder.append(", ");
        }
        if (parametersBuilder.length() > 0) {
            parametersBuilder.deleteCharAt(parametersBuilder.length() - 1);
            parametersBuilder.deleteCharAt(parametersBuilder.length() - 1);
        }
        b.append(parametersBuilder);
        b.append(") {\n");

        for (CodeElement blockLine : getBlockLines()) {
            blockLine.build(b, indentationLevel + 1);
        }
        b.append(baseTab);
        b.append("}\n");
    }

    private List<Return> getAllReturnLines(List<CodeElement> elements) {
        final List<Return> result = new ArrayList<>();
        for (CodeElement element : elements) {
            if (element instanceof Block) {
                Block block = (Block) element;
                result.addAll(getAllReturnLines(block.getBlockLines()));
            } else if (element instanceof Return) {
                result.add((Return) element);
            }
        }
        return result;
    }


}

