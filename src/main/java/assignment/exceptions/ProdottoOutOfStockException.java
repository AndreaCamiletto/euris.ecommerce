package assignment.exceptions;

public class ProdottoOutOfStockException extends RuntimeException {

    public ProdottoOutOfStockException(String messaggio) {
        super(messaggio);
    }
}
