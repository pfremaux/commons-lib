package commons.lib.tooling.java.v1.code.line;

import commons.lib.tooling.java.v1.CodeBehavior;

public class BulkLine extends CodeBehavior {
    private final String bulkLine;

    public BulkLine(String bulkLine) {
        this.bulkLine = bulkLine;
    }

    public String getBulkLine() {
        return bulkLine;
    }

    @Override
    public void build(StringBuilder builder, int indentationLevel) {
        final String baseTab = new String(new char[indentationLevel]).replace("\0", "\t");
        builder.append(baseTab);
        builder.append(bulkLine);
        builder.append(";\n");
    }
}