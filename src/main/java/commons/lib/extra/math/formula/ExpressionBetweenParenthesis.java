package commons.lib.extra.math.formula;

import commons.lib.extra.math.formula.interfaces.Operation;
import commons.lib.extra.math.formula.interfaces.OperationElement;

import java.math.BigDecimal;
import java.util.Map;

public final class ExpressionBetweenParenthesis  implements Operation, OperationElement {
    private final Formula formula;

    public ExpressionBetweenParenthesis(Formula formula) {
        this.formula = formula;
    }

    @Override
    public BigDecimal resolve(Map<String, BigDecimal> knowledge) {
        return formula.resolve(knowledge);
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

    public Formula getFormula() {
        return formula;
    }

    @Override
    public int getPriority() {
        return 1;
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
