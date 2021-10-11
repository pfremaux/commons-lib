package commons.lib.tooling.java.v2;

public class Assign  extends CodeElement{


    private final ObjectDeclaration newReceiver;
    private final ObjectUsage existingReceiver;
    private final CodeElement value;

    public Assign(CodeElement parent, ObjectDeclaration newReceiver, ObjectUsage existingReceiver, CodeElement value) {
        super(parent);
        this.newReceiver = newReceiver;
        this.existingReceiver = existingReceiver;
        this.value = value;
    }

    public ObjectDeclaration getNewReceiver() {
        return newReceiver;
    }

    public ObjectUsage getExistingReceiver() {
        return existingReceiver;
    }

    public CodeElement getValue() {
        return value;
    }

    @Override
    protected void build(StringBuilder b, String baseTab, int indentationLevel) {
        b.append(baseTab);
        StringBuilder valueBuilder = new StringBuilder();
       if (newReceiver != null) {
           value.build(valueBuilder, 0);
           String strValue = valueBuilder.toString().trim();
           ObjectDeclaration receiveWithAssignment = newReceiver.newInstanceWithValue(strValue);
           receiveWithAssignment.build(b, 0);
       } else {
           if (value != null && value instanceof ObjectUsage) {
               ObjectUsage objectValue = (ObjectUsage) this.value;
               if (objectValue.getName() != null && objectValue.getName().equals(existingReceiver.getName())) {
                   b.append("this.");
               }
           }
           existingReceiver.build(b, 0);
           b.append(" = ");
           value.build(b, 0);
           b.append(";\n");
       }
    }
}
