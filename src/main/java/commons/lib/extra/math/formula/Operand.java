package commons.lib.extra.math.formula;

import commons.lib.extra.math.formula.interfaces.Operation;
import commons.lib.extra.math.formula.interfaces.OperationElement;
import commons.lib.main.StringUtils;
import commons.lib.main.console.As;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operand implements OperationElement, Operation {
    private final String name;
    private BigDecimal value;
    private Operation operation;


    public Operand(String name) {
        //if (name.chars().allMatch(Character::isDigit)) {
        if (StringUtils.isNumber(name)) {
            value = new BigDecimal(name);
        }
        this.name = name;
    }

    public static boolean hasValue(OperationElement op) {
        if (op instanceof Operand) {
            final Operand operand = As.any(op);
            return operand.getValue() != null;
        }
        return false;
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