package commons.lib.tooling.java.v2;

import java.util.function.Function;

public class ObjectUsage extends CodeElement {
    private final String name;
    private final boolean setNull;
    private final String value;
    private final ObjectDeclaration origin;
    private final Function<MethodDeclared, ObjectUsage> delayedResolution;

    public ObjectUsage(CodeElement parent, String name, boolean setNull, ObjectDeclaration origin) {
        super(parent);
        this.name = name;
        this.setNull = setNull;
        this.origin = origin;
        this.value = null;
        this.delayedResolution = null;
    }

    /**
     * Main constructor. Others might be deleted.
     * @param parent
     * @param name
     * @param value
     */
    public ObjectUsage(CodeElement parent, String name, String value, ObjectDeclaration origin) {
        super(parent);
        this.name = name;
        this.value = value;
        this.origin = origin;
        this.setNull = false;
        this.delayedResolution = null;
    }

    public ObjectUsage(Function<MethodDeclared, ObjectUsage> delayedResolution) {
        super(null);
        this.name = null;
        this.value = null;
        this.origin = null;
        this.setNull = false;
        this.delayedResolution = delayedResolution;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public ObjectDeclaration getOrigin() {
        return origin;
    }

    public boolean isDelayed() {
        return delayedResolution != null;
    }

    public ObjectUsage resolve(MethodDeclared m) {
        if (delayedResolution == null) {
            return null;
        }
        return delayedResolution.apply(m);
    }

    @Override
    protected void build(StringBuilder b, String baseTab, int indentationLevel) {
        if (setNull) {
            b.append("null");
        } else if (name == null) {
            b.append(value);
        } else {
            b.append(name);
        }
    }
}
