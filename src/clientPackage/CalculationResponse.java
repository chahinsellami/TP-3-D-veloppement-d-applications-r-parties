package clientPackage;

import java.io.Serializable;

public class CalculationResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String message;

    public CalculationResponse(String message) {
        this.message = message;
    }

    public static CalculationResponse success(OperationRequest operation, double result) {
        String formattedResult = result == (long) result
                ? Long.toString((long) result)
                : Double.toString(result);
        return new CalculationResponse(operation + " = " + formattedResult);
    }

    public static CalculationResponse error(String message) {
        return new CalculationResponse(message);
    }

    public String getMessage() {
        return message;
    }
}
