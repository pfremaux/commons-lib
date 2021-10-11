package commons.lib.tooling.java.v1.code.block;

import commons.lib.tooling.java.v1.CodeBehavior;
import commons.lib.tooling.java.v1.code.CodeMethod;
import commons.lib.tooling.java.v1.code.CodeObject;
import commons.lib.tooling.java.v1.code.ReferenceClass;
import commons.lib.tooling.java.v1.code.line.DeclareObject;
import commons.lib.tooling.java.v1.code.line.MethodCall;
import commons.lib.tooling.java.v1.code.line.Return;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TryBlock extends Block {

    private ReferenceClass returnType;
    private final List<CodeBehavior> lines = new ArrayList<>();
    private final List<ReferenceClass> typesReferenced = new ArrayList<>();


    public TryBlock declare(CodeObject object, boolean isFinal) {
        lines.add(new DeclareObject(isFinal, object));
        typesReferenced.add(object.getReferenceClass());
        return this;
    }

    public TryBlock call(Function<CodeObject, CodeMethod> call, CodeObject callingObject, List<CodeObject> parameters, CodeObject storedIn) {
        lines.add(new MethodCall(call, callingObject, parameters, storedIn));
        return this;
    }

    public TryBlock call(Function<ReferenceClass, CodeMethod> call, ReferenceClass callingClass, List<CodeObject> parameters, CodeObject storedIn) {
        lines.add(new MethodCall(call, callingClass, parameters, storedIn));
        return this;
    }


    public TryBlock returnObject(CodeObject obj) {
        lines.add(new Return(obj));
        // TODO PFR gerer abstract
        this.returnType = obj.getReferenceClass();
        typesReferenced.add(returnType);
        return this;
    }

    public TryBlock returnObject(Return returnAction) {
        lines.add(returnAction);
        // TODO PFR gerer abstract
        this.returnType = returnAction.getObjectReturned().getReferenceClass();
        return this;
    }

    @Override
    public void build(StringBuilder builder, int indentationLevel) {
        // TODO PFR indent
        builder.append("try {");
        for (CodeBehavior line : lines) {
            line.build(builder, indentationLevel+1);
        }
        builder.append("}");
    }
}
