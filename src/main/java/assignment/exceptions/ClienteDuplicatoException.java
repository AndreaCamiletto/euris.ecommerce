package assignment.exceptions;

public class ClienteDuplicatoException extends RuntimeException {
    public ClienteDuplicatoException(String messaggio) {
        super(messaggio);
    }
}
