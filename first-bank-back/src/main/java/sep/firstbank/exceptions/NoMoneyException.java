package sep.firstbank.exceptions;

public class NoMoneyException extends Exception {
    private static final String message = "NO MONEY!";

    public NoMoneyException() {
        super(message);
    }
}
