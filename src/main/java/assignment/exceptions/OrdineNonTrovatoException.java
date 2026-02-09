package assignment.exceptions;

public class OrdineNonTrovatoException extends RuntimeException{
    public OrdineNonTrovatoException(String messaggio) {
        super(messaggio);
    }
}
