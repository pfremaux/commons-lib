package commons.lib.extra.math.formula;

import commons.lib.extra.math.formula.interfaces.Operation;
import commons.lib.extra.math.formula.interfaces.OperationElement;
import commons.lib.main.console.As;

import java.math.BigDecimal;
import java.util.Map;

public final class
ExpressionBetweenParenthesis  implements Operation, OperationElement {
    private final Formula formula;

    public ExpressionBetweenParenthesis(Formula formula) {
        this.formula = formula;
    }

    @Override
    public Operation simplify(int level, Map<String, BigDecimal> knowledge) {
        final Operation simplify = formula.simplify(level, knowledge);
        if (simplify instanceof Formula) {
            final Formula formula = (Formula) simplify;
            if (formula.getOperationElements().size()>1) {
                return new ExpressionBetweenParenthesis(formula);
            }
        }
        return simplify;
    }
    public BigDecimal resolve(Map<String, BigDecimal> knowledge) {
        final Operation simplify = this
                .simplify(0, knowledge)
                .simplify(1, knowledge)
                .simplify(2, knowledge)
                ;
        if (simplify instanceof Operand) {
            final Operand operand = As.any(simplify);
            // Might be null if the library is buggy but we're expecting the operation to be resolved.
            return operand.getValue();
        }
        return null;
    }

    public Formula getFormula() {
        return formula;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean isRequireLeft() {
        return false;
    }

    @Override
    public boolean isRequireRight() {
        return false;
    }

    @Override
    public String toString() {
        return "(" + formula + ")";
    }
}
