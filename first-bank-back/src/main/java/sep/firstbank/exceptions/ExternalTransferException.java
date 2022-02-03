package sep.firstbank.exceptions;

public class ExternalTransferException extends Exception {
    private static final String message = "External Transfer was denied!";

    public ExternalTransferException() {
        super(message);
    }
}
