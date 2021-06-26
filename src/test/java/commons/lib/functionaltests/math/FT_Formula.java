package commons.lib.functionaltests.math;

import commons.lib.extra.math.formula.ExpressionBetweenParenthesis;
import commons.lib.extra.math.formula.Formula;
import commons.lib.extra.math.formula.Operand;
import commons.lib.extra.math.formula.interfaces.Operation;
import commons.lib.main.console.As;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.Map;

public class FT_Formula {

    public static void main(String[] args) {
        BigDecimal result ;
        ExpressionBetweenParenthesis instance;
        result = resolveProperly(Formula.getInstance("1+1"));
        Assert.assertEquals(2, result.intValue());

        instance = Formula.getInstance("4+3*2");
        result = resolveProperly(instance);
        Assert.assertEquals(10, result.intValue());

        instance = Formula.getInstance("(4*(3-1)*2)");
        result = resolveProperly(instance);
        Assert.assertEquals(16, result.intValue());

        instance = Formula.getInstance("sin(1/5)");
        result = resolveProperly(instance);
        Assert.assertEquals(0.19, result.doubleValue(), 0.01);

        instance = Formula.getInstance("sin(0.2)");
        result = resolveProperly(instance);
        Assert.assertEquals(0.19, result.doubleValue(), 0.01);
    }

    private static BigDecimal resolveProperly(ExpressionBetweenParenthesis instance) {
        final Operation simplify = instance
                .simplify(0, Map.of())
                .simplify(1, Map.of())
                .simplify(2, Map.of())
                ;
        if (simplify instanceof Operand) {
            final Operand operand = As.any(simplify);
            // Might be null if the library is buggy but we're expecting the operation to be resolved.
            return operand.getValue();
        }
        return null;
    }

}
