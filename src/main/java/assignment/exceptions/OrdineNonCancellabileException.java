package assignment.exceptions;

public class OrdineNonCancellabileException extends RuntimeException {
    public OrdineNonCancellabileException(String messaggio) {
        super(messaggio);
    }
}
