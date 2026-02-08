package assignment.exceptions;

public class ProdottoDuplicatoException extends RuntimeException  {

    public ProdottoDuplicatoException(String messaggio) {
        super(messaggio);
    }
}