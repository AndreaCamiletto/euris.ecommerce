package assignment.controller;

import assignment.exceptions.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClienteDuplicatoException.class)
    public ResponseEntity<Map<String, String>> handleClienteDuplicato(ClienteDuplicatoException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ProdottoDuplicatoException.class)
    public ResponseEntity<Map<String, String>> handleProdottoDuplicato(ProdottoDuplicatoException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(OrdineDuplicatoException.class)
    public ResponseEntity<Map<String, String>> handleOrdineDuplicato(OrdineDuplicatoException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ProdottoNonTrovatoException.class)
    public ResponseEntity<Map<String, String>> handleProdottoNonTrovato(ProdottoNonTrovatoException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ProdottoOutOfStockException.class)
    public ResponseEntity<Map<String, String>> handleProdottoOutOfStock(ProdottoOutOfStockException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(OrdineNonTrovatoException.class)
    public ResponseEntity<Map<String, String>> handleOrdineNonTrovato(OrdineNonTrovatoException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(OrdineNonCancellabileException.class)
    public ResponseEntity<Map<String, String>> handleOrdineNonCancellabile(OrdineNonCancellabileException ex) {
        Map<String, String> body = Map.of(
                "error", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
