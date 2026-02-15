package assignment.controller;

import assignment.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    static class ExceptionTestController {
        @GetMapping("/test/cliente-duplicato")
        public void throwClienteDup() { throw new ClienteDuplicatoException("duplicato"); }

        @GetMapping("/test/prodotto-duplicato")
        public void throwProdDup() { throw new ProdottoDuplicatoException("duplicato"); }

        @GetMapping("/test/ordine-duplicato")
        public void throwOrdDup() { throw new OrdineDuplicatoException("duplicato"); }

        @GetMapping("/test/prodotto-non-trovato")
        public void throwProdNotFound() { throw new ProdottoNonTrovatoException("non trovato"); }

        @GetMapping("/test/stock")
        public void throwStock() { throw new ProdottoOutOfStockException("esaurito"); }

        @GetMapping("/test/ordine-non-trovato")
        public void throwOrdNotFound() { throw new OrdineNonTrovatoException("non trovato"); }

        @GetMapping("/test/ordine-non-cancellabile")
        public void throwNonCan() { throw new OrdineNonCancellabileException("no"); }

        @GetMapping("/test/ordine-non-avanzabile")
        public void throwNonAva() { throw new OrdineNonAvanzabileException("no"); }

        @GetMapping("/test/cliente-non-trovato")
        public void throwCliNotFound() { throw new ClienteNonTrovatoException("non trovato"); }

        @GetMapping("/test/generica")
        public void throwGeneric() { throw new RuntimeException("boom"); }

        @GetMapping("/test/validazione")
        public void throwValidation() throws MethodArgumentNotValidException {
            throw new MethodArgumentNotValidException(
                    new MethodParameter(this.getClass().getDeclaredMethods()[0], -1),
                    new BeanPropertyBindingResult(new Object(), "oggetto")
            );
        }
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ExceptionTestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testHandleClienteDuplicato() throws Exception {
        mockMvc.perform(get("/test/cliente-duplicato"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("duplicato"));
    }

    @Test
    public void testHandleProdottoDuplicato() throws Exception {
        mockMvc.perform(get("/test/prodotto-duplicato"))
                .andExpect(status().isConflict());
    }

    @Test
    public void testHandleOrdineDuplicato() throws Exception {
        mockMvc.perform(get("/test/ordine-duplicato"))
                .andExpect(status().isConflict());
    }

    @Test
    public void testHandleProdottoNonTrovato() throws Exception {
        mockMvc.perform(get("/test/prodotto-non-trovato"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testHandleOutOfStock() throws Exception {
        mockMvc.perform(get("/test/stock"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testHandleOrdineNonTrovato() throws Exception {
        mockMvc.perform(get("/test/ordine-non-trovato"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testHandleOrdineNonCancellabile() throws Exception {
        mockMvc.perform(get("/test/ordine-non-cancellabile"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testHandleOrdineNonAvanzabile() throws Exception {
        mockMvc.perform(get("/test/ordine-non-avanzabile"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testHandleClienteNonTrovato() throws Exception {
        mockMvc.perform(get("/test/cliente-non-trovato"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testHandleGenericException() throws Exception {
        mockMvc.perform(get("/test/generica"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Errore interno del server"));
    }

    @Test
    public void testHandleValidationExceptions() throws Exception {
        mockMvc.perform(get("/test/validazione"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}