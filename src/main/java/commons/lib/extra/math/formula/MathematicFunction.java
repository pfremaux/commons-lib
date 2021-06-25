package commons.lib.extra.math.formula;

import commons.lib.extra.math.formula.interfaces.Operation;
import commons.lib.extra.math.formula.interfaces.OperationElement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class MathematicFunction  implements OperationElement, Operation {

    static Map<String, BiFunction<BigDecimal, BigDecimal, BigDecimal>> functionsNames = Map.of(
            "cos", (BigDecimal a, BigDecimal b) -> BigDecimal.valueOf(Math.cos(a.doubleValue())),
            "sin", (BigDecimal a, BigDecimal b) -> BigDecimal.valueOf(Math.sin(a.doubleValue())));

    private final String name;
    private final BiFunction<BigDecimal, BigDecimal, BigDecimal> function;
    private final ExpressionBetweenParenthesis subExpressionBetweenParenthesis;

    public MathematicFunction(String name, BiFunction<BigDecimal, BigDecimal, BigDecimal> function, ExpressionBetweenParenthesis subExpressionBetweenParenthesis) {
        this.name = name;
        this.function = function;
        this.subExpressionBetweenParenthesis = subExpressionBetweenParenthesis;
    }

    @Override
    public BigDecimal resolve(Map<String, BigDecimal> knowledge) {
        BigDecimal resolve = subExpressionBetweenParenthesis.resolve(knowledge);
        return function.apply(resolve, null);
    }

    @Override
    public Operation simplify(int level, Map<String, BigDecimal> knowledge) {
        if (level <= getPriority()) {
            return new Operand(resolve(knowledge));
        }
        return new MathematicFunction(
                name,
                getFunction(),
                new ExpressionBetweenParenthesis(new Formula(List.of(subExpressionBetweenParenthesis.simplify(level, knowledge)))));
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String toString() {
        return name + subExpressionBetweenParenthesis;
    }

    public String getName() {
        return name;
    }

    public BiFunction<BigDecimal, BigDecimal, BigDecimal> getFunction() {
        return function;
    }

    public ExpressionBetweenParenthesis getSubExpressionBetweenParenthesis() {
        return subExpressionBetweenParenthesis;
    }

    @Override
    public boolean isRequireLeft() {
        return false;
    }

    @Override
    public boolean isRequireRight() {
        return false;
    }
}