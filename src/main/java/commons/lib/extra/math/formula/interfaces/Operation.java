package commons.lib.extra.math.formula.interfaces;

import java.math.BigDecimal;
import java.util.Map;

public interface Operation extends OperationElement {
    //BigDecimal resolve(Map<String, BigDecimal> knowledge) ;
    Operation simplify(int level, Map<String, BigDecimal> knowledge) ;

    int getPriority();
}
