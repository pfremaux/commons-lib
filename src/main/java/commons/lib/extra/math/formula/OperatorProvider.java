package commons.lib.extra.math.formula;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class OperatorProvider {
    private static Map<Character, Operator> operators = new HashMap<>();

    static {
        operators.put('+', new Operator('+', 2, BigDecimal::add, false, true));
        operators.put('-', new Operator('-', 2, BigDecimal::subtract, false, true));
        operators.put('*', new Operator('*', 0, BigDecimal::multiply, true, true));
        operators.put('/', new Operator('/', 0, (a, b) -> a.divide(b, RoundingMode.HALF_EVEN), true, true));
    }

    public static Operator get(Character c) {
        return operators.get(c);
    }
}
