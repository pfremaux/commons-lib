package commons.lib.extra.math.formula;

import commons.lib.extra.math.formula.interfaces.Operation;
import commons.lib.extra.math.formula.interfaces.OperationElement;
import commons.lib.main.console.As;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Formula implements Operation {

    private final List<OperationElement> operationElements;

    public Formula(List<OperationElement> operationElements) {
        this.operationElements = operationElements;
    }

    public static void main(String[] args) {
        final ExpressionBetweenParenthesis expressionBetweenParenthesis = analyzeGroup("c*(a+b)+sin(c)".toCharArray(), 0);
        System.out.println(expressionBetweenParenthesis);
        final Map<String, BigDecimal> knowledge = Map.of("a", BigDecimal.ONE, "b", BigDecimal.ONE, "c", BigDecimal.ONE);
        //BigDecimal resolve = expressionBetweenParenthesis.resolve(knowledge);
        final Operation resolve = expressionBetweenParenthesis.simplify(0, knowledge);
        System.out.println(resolve);
    }

   /* @Deprecated
    @Override
    public BigDecimal resolve(Map<String, BigDecimal> knowledge) {
        BigDecimal result = BigDecimal.ZERO;
        Operator currentOperator = OperatorProvider.get('+');
        for (OperationElement operationElement : operationElements) {
            if (operationElement instanceof Operator) {
                currentOperator = (Operator) operationElement;
            } else if (operationElement instanceof Operation) {
                final Operation operation = (Operation) operationElement;
                final BigDecimal resolve = operation.resolve(knowledge);
                *//*if (currentOperator == null) {
                    continue;// TODO PFR tempo
                }*//*
                result = currentOperator.calculate(result, resolve);
                currentOperator = null;
            } else {
                System.out.println("Un processed element : " + operationElement);
            }
        }
        return result;
    }*/

    @Override
    public Operation simplify(int level, Map<String, BigDecimal> knowledge) {
        final List<OperationElement> result = new ArrayList<>();
        for (int i = 0; i < operationElements.size(); i++) {
            final OperationElement operationElement = operationElements.get(i);
            if (operationElement instanceof Operand) {
                result.add(((Operand) operationElement).simplify(level, knowledge));
            } else if (operationElement instanceof Operator) {
                final Operator operator = (Operator) operationElement;
                int priority = operator.getPriority();
                if (priority <= level) {
                    final Operation operationBefore = (Operation) operationElements.get(i - 1);
                    final Operation simplifiedBefore;
                    if (!result.isEmpty() && Operand.hasValue(result.get(result.size() - 1))) {
                        simplifiedBefore = As.any(result.get(result.size() - 1));
                    } else {
                        simplifiedBefore = operationBefore.simplify(level, knowledge);
                    }

                    final Operation operationAfter = (Operation) operationElements.get(i + 1);
                    final Operation simplifiedAfter = operationAfter.simplify(level, knowledge);
                    if (simplifiedBefore instanceof Operand && simplifiedAfter instanceof Operand) {
                        final Operand previousOperandElement = (Operand) simplifiedBefore;
                        final Operand nextOperandElement = (Operand) simplifiedAfter;
                        if (previousOperandElement.getValue() != null) {
                            result.remove(result.size() - 1);
                            final BigDecimal calculated = operator.calculate(previousOperandElement, nextOperandElement);
                            result.add(new Operand(calculated));
                            i++;
                        } else {
                            result.add(operator);
                        }
                    } else {
                        result.add(operator);
                    }
                    if (operator.getC() == '*') {
                        int newI = simplifyBasicMultiply(i, simplifiedBefore, simplifiedAfter, result);
                        if (newI == i) {
                            // nothing has been simplified
                        } else {
                            i = newI;
                        }
                    } else if (simplifiedAfter instanceof Operand && operator.getC() == '/') {
                        final Operand nextOperandElement = (Operand) simplifiedAfter;
                        if (BigDecimal.ONE.equals(nextOperandElement.getValue())) {
                            i++;
                            result.remove(result.size() - 1);
                        }
                    }
                } else {
                    result.add(operationElement);
                }
            } else if (operationElement instanceof MathematicFunction) {
                MathematicFunction mathematicFunction = (MathematicFunction) operationElement;
                result.add(mathematicFunction.simplify(level, knowledge));
            } else if (operationElement instanceof Formula) {
                result.add(((Formula) operationElement).simplify(level, knowledge));
            } else if (operationElement instanceof ExpressionBetweenParenthesis) {
                final ExpressionBetweenParenthesis expression = (ExpressionBetweenParenthesis) operationElement;
                final Operation simplify = expression.simplify(level, knowledge);
                result.add(simplify);
            } else {
                // TODO better exception
                throw new RuntimeException("Unexpected element : " + operationElement);
            }
        }
        if (result.size() == 1) {
            if (result.get(0) instanceof Operand) {
                final Operand operand = (Operand) result.get(0);
                if (operand.getValue() != null) {
                    return operand;
                }
            }
        }
        return new Formula(result);
    }

    private int simplifyBasicMultiply(int i, Operation operationBeforeStar, Operation operationAfterStar, List<OperationElement> mutableResult) {
        // IF 1 * x then '1 *' is useless
        if (operationBeforeStar instanceof Operand) {
            final Operand previousOperandElement = (Operand) operationBeforeStar;
            if (BigDecimal.ONE.equals(previousOperandElement.getValue())) {
                // Remove the previous element already saved in the result list
                mutableResult.remove(mutableResult.size() - 1);
            }
        } else if (operationAfterStar instanceof Operand) {
            // IF x * 1 then '* 1' is useless
            final Operand nextOperandElement = (Operand) operationAfterStar;
            if (BigDecimal.ONE.equals(nextOperandElement.getValue())) {
                // Bypass the next element as we know if equal to 1 and it's useless
                i++;
            }
        }
        return i;
    }

    private boolean isResolved(Operation o) {
        return o instanceof Operand && ((Operand) o).getValue() != null;
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
    public int getPriority() {
        return 0;
    }

    @Override
    public String toString() {
        return operationElements.stream().map(Object::toString).collect(Collectors.joining());
    }

    public void addOperationElement(OperationElement element) {
        operationElements.add(element);
    }


    public static ExpressionBetweenParenthesis getInstance(String strFormula) {
        return analyzeGroup(strFormula.toCharArray(), 0);
    }

    private static ExpressionBetweenParenthesis analyzeGroup(char[] chars, int j) {
        final Formula formula = new Formula(new ArrayList<>());
        StringBuffer letters = new StringBuffer();
        int byPass = 0;
        for (int i = j; i < chars.length; i++) {

            if (chars[i] == '(' && byPass == 0) {
                String entity = letters.toString();
                if (entity.length() > 0 && (MathematicFunction.functionsNames.containsKey(entity))) {
                    final ExpressionBetweenParenthesis subExpressionBetweenParenthesis = analyzeGroup(chars, i + 1);
                    formula.addOperationElement(new MathematicFunction(entity, MathematicFunction.functionsNames.get(entity), subExpressionBetweenParenthesis));
                    letters = new StringBuffer();
                    byPass++;
                } else {
                    final ExpressionBetweenParenthesis subExpressionBetweenParenthesis = analyzeGroup(chars, i + 1);
                    formula.addOperationElement(subExpressionBetweenParenthesis);
                    byPass++;
                }
            } else if (chars[i] == '(' && byPass > 0) {
                byPass++;
            } else if (chars[i] == ')') {
                if (byPass > 0) {
                    byPass--;
                    continue;
                }
                if (letters.length() > 0) {
                    formula.addOperationElement(new Operand(letters.toString()));
                }
                return new ExpressionBetweenParenthesis(formula);
            } else if (byPass > 0) {
                continue;
            } else if (Character.isAlphabetic(chars[i])) {
                letters.append(chars[i]);
            } else if (Character.isDigit(chars[i]) || chars[i] == '.') {
                if (i == 0) {
                    letters.append(chars[i]);
                } else {
                    letters.append(chars[i]);
                }
            } else {
                String entity = letters.toString();
                letters = new StringBuffer();

                if (MathematicFunction.functionsNames.containsKey(entity)) {
                    // TODO is it necessary
                } else if (entity.length() > 0) {
                    formula.addOperationElement(new Operand(entity));
                }
                if (isOperator(chars[i])) {
                    formula.addOperationElement(OperatorProvider.get(chars[i]));
                } else {
                    // TODO better exception
                    throw new RuntimeException("Unexpected character : " + chars[i]);
                }
            }
        }
        if (letters.length() > 0) {
            formula.addOperationElement(new Operand(letters.toString()));
        }
        return new ExpressionBetweenParenthesis(formula);
    }

    private static boolean isOperator(char aChar) {
        return Arrays.asList('+', '-', '*', '/').contains(aChar);
    }

    public List<OperationElement> getOperationElements() {
        return operationElements;
    }

}
