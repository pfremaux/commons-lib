package commons.lib.tooling.java.v2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OperationCode extends CodeElement {
    private final List<String> operators;
    private final List<CodeElement> operands;
    public OperationCode(CodeElement parent, List<String> operators, List<CodeElement> operands) {
        super(parent);
        this.operators = operators;
        this.operands = operands;
    }


    @Override
    protected void build(StringBuilder b, String baseTab, int indentationLevel) {
        // indentationLevel should be ignored
        Iterator<CodeElement> operandsIt = operands.iterator();
        Iterator<String> operatorsIt = operators.iterator();
        operandsIt.next().build(b, 0);
        while (operandsIt.hasNext()) {
            final String operator = operatorsIt.next();
            b.append(" ");
            b.append(operator);
            b.append(" ");
            operandsIt.next().build(b, 0);
        }

    }

    public static class Accumulator {
        private final List<String> operators = new ArrayList<>();
        private final List<CodeElement> operands = new ArrayList<>();
        public static Accumulator accumulate(CodeElement firstElement) {
            Accumulator accumulator = new Accumulator();
            accumulator.operands.add(firstElement);
            return accumulator;
        }
        public Accumulator add(String operator) {
            operators.add(operator);
            return this;
        }
        public Accumulator add(CodeElement o) {
            operands.add(o);
            return this;
        }

        public OperationCode build(CodeElement parent) {
            return new OperationCode(parent, operators, operands);
        }
    }
}
