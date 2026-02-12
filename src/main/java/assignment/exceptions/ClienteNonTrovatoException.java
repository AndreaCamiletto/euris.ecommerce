package assignment.exceptions;

public class ClienteNonTrovatoException extends RuntimeException{
    public ClienteNonTrovatoException(String messaggio) {
        super(messaggio);
    }
}
