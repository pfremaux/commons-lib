package commons.lib.tooling.java.v1.code.line;

import commons.lib.tooling.java.v1.CodeBehavior;
import commons.lib.tooling.java.v1.code.CodeMethod;
import commons.lib.tooling.java.v1.code.CodeObject;
import commons.lib.tooling.java.v1.code.ReferenceClass;

import java.util.List;
import java.util.function.Function;

public class  MethodCall extends CodeBehavior {
    private final Function<CodeObject, CodeMethod> call;
    private final CodeObject callingObject;
    private final ReferenceClass callingClass;
    private final CodeMethod methodCalled;
    private final List<CodeObject> parameters;
    private final CodeObject storedIn;

    public MethodCall(Function<CodeObject, CodeMethod> call, CodeObject callingObject, List<CodeObject> parameters, CodeObject storedIn) {
        this.call = call;
        this.callingObject = callingObject;
        this.callingClass = null;
        this.methodCalled = call.apply(callingObject);
        this.parameters = parameters;
        this.storedIn = storedIn;
    }


    public MethodCall(Function<ReferenceClass, CodeMethod> call, ReferenceClass callingClass, List<CodeObject> parameters, CodeObject storedIn) {
        this.call = null;
        this.callingObject = null;
        this.callingClass = callingClass;
        this.methodCalled = call.apply(callingClass);
        this.parameters = parameters;
        this.storedIn = storedIn;
    }

    public Function<CodeObject, CodeMethod> getCall() {
        return call;
    }

    public List<CodeObject> getParameters() {
        return parameters;
    }

    public CodeObject getStoredIn() {
        return storedIn;
    }

    @Override
    public void build(StringBuilder builder, int indentationLevel) {
        final String baseTab = new String(new char[indentationLevel]).replace("\0", "\t");
        builder.append(baseTab);
        if (storedIn != null) {
            builder.append(storedIn.getName());
            builder.append(" = ");
        }
        builder.append(callingObject.getName());
        builder.append(".");
        builder.append(methodCalled.getName());
        builder.append("(");
        // TODO PFR validate parameter fit
        StringBuilder parametersBuilder = new StringBuilder();
        parameters.forEach(codeObject -> {
            parametersBuilder.append(codeObject.getName());
            parametersBuilder.append(", ");
        });
        if (parametersBuilder.length() > 0) {
            parametersBuilder.deleteCharAt(parametersBuilder.length() - 1);
            parametersBuilder.deleteCharAt(parametersBuilder.length() - 1);
            builder.append(parametersBuilder);
        }
        builder.append(");\n");
    }
}