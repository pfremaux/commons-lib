package commons.lib.extra.math.formula;

import commons.lib.extra.math.formula.interfaces.OperationElement;

import java.math.BigDecimal;
import java.util.function.BiFunction;

public final class Operator implements OperationElement {
    private final char c;
    private final int priority;
    private final BiFunction<BigDecimal, BigDecimal, BigDecimal> compute;
    private final boolean isRequireLeft;
    private final boolean isRequireRight;

    public Operator(char c, int priority, BiFunction<BigDecimal, BigDecimal, BigDecimal> compute, boolean isRequireLeft, boolean isRequireRight) {
        this.c = c;
        this.priority = priority;
        this.compute = compute;
        this.isRequireLeft = isRequireLeft;
        this.isRequireRight = isRequireRight;
    }

    public int getPriority() {
        return priority;
    }

    public BiFunction<BigDecimal, BigDecimal, BigDecimal> getCompute() {
        return compute;
    }

    public BigDecimal calculate(BigDecimal a, BigDecimal b) {
        return compute.apply(a, b);
    }

    public BigDecimal calculate(Operand a, Operand b) {
        return compute.apply(a.getValue(), b.getValue());
    }

    public char getC() {
        return c;
    }

    @Override
    public String toString() {
        return String.valueOf(c);
    }

    @Override
    public boolean isRequireLeft() {
        return isRequireLeft;
    }

    @Override
    public boolean isRequireRight() {
        return isRequireRight;
    }
}
