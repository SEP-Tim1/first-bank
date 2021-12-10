package sep.firstbank.exceptions;

public class CreditCardNotFoundException extends Exception {
    private static final String message = "Credit card not found!";

    public CreditCardNotFoundException() {
        super(message);
    }
}
