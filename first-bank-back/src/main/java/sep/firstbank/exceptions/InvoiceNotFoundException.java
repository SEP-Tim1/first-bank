package sep.firstbank.exceptions;

public class InvoiceNotFoundException extends Exception {
    private static final String message = "Invoice not found!";

    public InvoiceNotFoundException() {
        super(message);
    }
}
