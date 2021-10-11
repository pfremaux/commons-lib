package commons.lib.tooling.java.v1.code;

import commons.lib.main.StringUtils;

public class CodeObject {
    private final ReferenceClass referenceClass;
    private final String name;

    public CodeObject(ReferenceClass referenceClass, String name) {
        this.referenceClass = referenceClass;
        this.name = name;
    }

    public ReferenceClass getReferenceClass() {
        return referenceClass;
    }

    public String getName() {
        return name;
    }

    public void build(StringBuilder builder) {
        if (referenceClass == null) {
            // then base type... number or string
            if (StringUtils.isNumber(name)) {
                builder.append(name);
            } else {
                builder.append("\"");
                builder.append(name);
                builder.append("\"");
            }
        } else {
            if (name.equals("String")) {
                builder.append("\"");
                builder.append(name);
                builder.append("\"");

            } else {
                builder.append(name);
            }
        }
    }
}
