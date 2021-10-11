package commons.lib.tooling.java.v1;

import commons.lib.tooling.java.v1.code.*;
import commons.lib.tooling.java.v1.code.block.Block;
import commons.lib.tooling.java.v1.code.line.DeclareObject;
import commons.lib.tooling.java.v1.code.line.MethodCall;
import commons.lib.tooling.java.v1.code.line.Return;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MethodGenerator {


    private final JavaFile parent;
    private final String name;
    private final List<CodeObject> parameters;
    private final Scope scope;
    private final List<CodeBehavior> lines = new ArrayList<>();
    private boolean isStatic;
    private ReferenceClass returnType;

    private final List<ReferenceClass> typesReferenced = new ArrayList<>();

    public MethodGenerator(JavaFile parent, Scope scope, String name, List<CodeObject> parameters) {
        this.parent = parent;
        this.name = name;
        this.parameters = parameters;
        this.scope = scope;
        typesReferenced.addAll(parameters.stream().map(CodeObject::getReferenceClass).collect(Collectors.toList()));
    }

    public MethodGenerator declare(CodeObject object, boolean isFinal) {
        lines.add(new DeclareObject(isFinal, object));
        typesReferenced.add(object.getReferenceClass());
        return this;
    }

    public MethodGenerator call(Function<CodeObject, CodeMethod> call, CodeObject callingObject, List<CodeObject> parameters, CodeObject storedIn) {
        lines.add(new MethodCall(call, callingObject,  parameters,  storedIn));
        return this;
    }

    public MethodGenerator isStatic() {
        isStatic = true;
        return this;
    }

    public MethodGenerator returnObject(CodeObject obj) {
        lines.add(new Return(obj));
        // TODO PFR gerer abstract
        this.returnType = obj.getReferenceClass();
        typesReferenced.add(returnType);
        return this;
    }

    public MethodGenerator returnObject(Return returnAction) {
        lines.add(returnAction);
        // TODO PFR gerer abstract
        this.returnType = returnAction.getObjectReturned().getReferenceClass();
        return this;
    }

    public MethodGenerator addBlock(Block block) {
        lines.add(block);
        return this;
    }


    public JavaFile endMethod() {
        CodeMethod method = new CodeMethod(isStatic, scope, name, returnType, parameters, lines, typesReferenced);
        parent.getMethods().add(method);
        parent.getReferenceClass().getCodeMethods().add(method);
        return parent;
    }
}
