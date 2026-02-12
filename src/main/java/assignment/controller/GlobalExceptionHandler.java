package assignment.controller;

import assignment.exceptions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(ClienteDuplicatoException.class)
    public ResponseEntity<Map<String, String>> handleClienteDuplicato(ClienteDuplicatoException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        log.warn("Tentativo di creazione cliente duplicato: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ProdottoDuplicatoException.class)
    public ResponseEntity<Map<String, String>> handleProdottoDuplicato(ProdottoDuplicatoException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        log.warn("Tentativo di creazione prodotto duplicato: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(OrdineDuplicatoException.class)
    public ResponseEntity<Map<String, String>> handleOrdineDuplicato(OrdineDuplicatoException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        log.warn("Tentativo di creazione ordine duplicato: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ProdottoNonTrovatoException.class)
    public ResponseEntity<Map<String, String>> handleProdottoNonTrovato(ProdottoNonTrovatoException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        log.error("Prodotto non trovato: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ProdottoOutOfStockException.class)
    public ResponseEntity<Map<String, String>> handleProdottoOutOfStock(ProdottoOutOfStockException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        log.error("Tentativo di ordine con quantita superiore allo stock: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(OrdineNonTrovatoException.class)
    public ResponseEntity<Map<String, String>> handleOrdineNonTrovato(OrdineNonTrovatoException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        log.error("Ordine non trovato: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(OrdineNonCancellabileException.class)
    public ResponseEntity<Map<String, String>> handleOrdineNonCancellabile(OrdineNonCancellabileException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(OrdineNonAvanzabileException.class)
    public ResponseEntity<Map<String, String>> handleOrdineNonAvanzabile(OrdineNonAvanzabileException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        log.warn("Violazione regola di business: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        log.warn("Errore di validazione input: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ClienteNonTrovatoException.class)
    public ResponseEntity<Map<String, String>> handleClienteNonTrovato(ClienteNonTrovatoException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        log.error("Cliente non trovato: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        Map<String, String> body = Map.of(
                "error", "Errore interno del server"
        );
        log.error("ERRORE NON GESTITO: ", ex);
        return ResponseEntity.internalServerError().body(body);
    }
}
