package clientPackage;

import java.io.Serializable;

public class OperationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int firstOperand;
    private final String operator;
    private final int secondOperand;

    public OperationRequest(int firstOperand, String operator, int secondOperand) {
        this.firstOperand = firstOperand;
        this.operator = operator;
        this.secondOperand = secondOperand;
    }

    public int getFirstOperand() {
        return firstOperand;
    }

    public String getOperator() {
        return operator;
    }

    public int getSecondOperand() {
        return secondOperand;
    }

    @Override
    public String toString() {
        return firstOperand + " " + operator + " " + secondOperand;
    }
}
