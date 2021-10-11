package commons.lib.tooling.java.v2;

import java.util.List;

public class Call extends CodeElement {

    private final ClassDefinition classDef;
    private final ObjectUsage objectCaller;
    private ObjectDeclaration declaration;
    private MethodDeclared methodDeclared;
    private final String methodName;
    private final List<ObjectUsage> parameters;


    public Call(CodeElement parent, ClassDefinition classDef, ObjectUsage objectCaller, MethodDeclared methodDeclared, String methodName, List<ObjectUsage> parameters) {
        super(parent);
        this.classDef = classDef;
        this.objectCaller = objectCaller;
        this.methodDeclared = methodDeclared;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public Call(ObjectUsage objectCaller, String methodName, List<ObjectUsage> parameters) {
        this(null, null, objectCaller, null, methodName, parameters);
    }

    public Call(ClassDefinition classDef, String staticMethodName, List<ObjectUsage> parameters) {
        this(null, classDef, null, null, staticMethodName, parameters);
    }

    public CallResult and() {
        return new CallResult(this);
    }

    public static class CallResult {
        private final Call call;

        public CallResult(Call call) {
            this.call = call;
        }

        public Block setResultIn(ObjectDeclaration objectDeclaration) {
            call.declaration = objectDeclaration;
            return (Block) call.getParent();
        }

    }


    public ObjectUsage getObjectCaller() {
        return objectCaller;
    }

    public MethodDeclared getMethodDeclared() {
        return methodDeclared;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<ObjectUsage> getParameters() {
        return parameters;
    }

    @Override
    protected void build(StringBuilder b, String baseTab, int indentationLevel) {
        if (declaration != null) {
            declaration.build(b, indentationLevel);
        } else {
            b.append(baseTab);
        }
        if (objectCaller != null) {
            b.append(objectCaller.getName());
            b.append(".");
        }
        if (methodName != null) {
            if (classDef != null) {
                b.append(classDef.getName());
                b.append(".");
                methodDeclared = classDef.getBlockMethods().get(methodName);
            }
            /*legacy ? MethodDeclared parent = getMethod();
            ClassDefinition classDef = (ClassDefinition) parent.getParent();
            methodDeclared = classDef.getBlockMethods().get(methodName);*/
            //methodDeclared = getMethod();
        }
        b.append(methodDeclared.getName());
        b.append("(");
        for (ObjectUsage parameter : parameters) {
            parameter.build(b, 0);
            b.append(", ");
        }
        if (!parameters.isEmpty()) {
            b.deleteCharAt(b.length() - 1);
            b.deleteCharAt(b.length() - 1);
        }
        b.append(");\n");
    }


}
