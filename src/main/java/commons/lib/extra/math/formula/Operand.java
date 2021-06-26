package commons.lib.extra.math.formula;

import commons.lib.extra.math.formula.interfaces.Operation;
import commons.lib.extra.math.formula.interfaces.OperationElement;

import java.math.BigDecimal;
import java.util.Map;

public class Operand implements OperationElement, Operation {
    private final String name;
    private BigDecimal value;
    private Operation operation;

    public Operand(String name) {
        if (name.chars().allMatch(Character::isDigit)) {
            value = new BigDecimal(name);
        }
        this.name = name;
    }


    public Operand(BigDecimal value) {
        this.name = value.toString();
        this.value = value;
    }


    @Override
    public Operation simplify(int level, Map<String, BigDecimal> knowledge) {
        BigDecimal resolvedNullable = getValue();
        if (resolvedNullable == null) {
            resolvedNullable = knowledge.get(name);
        }
        return resolvedNullable == null ? this : new Operand(resolvedNullable);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Operation getOperation() {
        return operation;
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
        return name != null ? name : value.toString();
    }
}