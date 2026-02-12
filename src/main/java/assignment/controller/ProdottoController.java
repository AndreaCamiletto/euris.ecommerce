package assignment.controller;

import assignment.dto.request.ProdottoRequestDTO;
import assignment.dto.response.ProdottoResponseDTO;
import assignment.service.ProdottoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prodotti")
@Tag(name = "Prodotti", description = "Gestione delle anagrafiche prodotti")
public class ProdottoController {

    private static final Logger log = LoggerFactory.getLogger(ProdottoController.class);

    private final ProdottoService prodottoService;

    public ProdottoController(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
    }

    @Operation(
            summary = "Recupera la lista dei prodotti",
            description = "Restituisce tutti i prodotti.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista recuperata con successo")
            }
    )
    @GetMapping
    public ResponseEntity<?> getListaProdotti(@ParameterObject Pageable pageable,
                                              @RequestParam(required = false) Integer page) {
        if (page == null) {
            log.info("Richiesta catalogo completo prodotti");
            return ResponseEntity.ok(prodottoService.getListaProdotti());
        }
        log.info("Richiesta catalogo prodotti paginato: pagina {}, dimensione {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(prodottoService.getListaProdotti(pageable));
    }

    @Operation(
            summary = "Crea un nuovo prodotto",
            description = "Inserisce un nuovo prodotto nel sistema. Restituisce 409 se il codice prodotto è già presente.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Prodotto creato con successo"),
                    @ApiResponse(responseCode = "400", description = "Dati di input non validi"),
                    @ApiResponse(responseCode = "409", description = "Prodotto già esistente, codice prodotto duplicato)")
            }
    )
    @PostMapping
    public ResponseEntity<ProdottoResponseDTO> aggiungiProdotto(@Valid @RequestBody ProdottoRequestDTO prodotto) {
        log.info("Inserimento nuovo prodotto nel catalogo: {} (Codice: {})",
                prodotto.nome(), prodotto.codProdotto());
        ProdottoResponseDTO inserito = prodottoService.aggiungiProdotto(prodotto);
        log.info("Prodotto inserito con successo con ID: {}", inserito.codProdotto());
        return ResponseEntity.status(HttpStatus.CREATED).body(inserito);
    }

    @Operation(
            summary = "Ricerca un prodotto per codice prodotto",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Prodotto trovato"),
                    @ApiResponse(responseCode = "404", description = "Prodotto non trovato", content = @Content)
            }
    )
    @GetMapping("/{codProdotto}")
    public ResponseEntity<ProdottoResponseDTO> getProdotto(@PathVariable String codProdotto) {
        log.debug("Ricerca dettaglio prodotto per codice: {}", codProdotto);
        return ResponseEntity.ok(prodottoService.getProdotto(codProdotto));
    }
}
