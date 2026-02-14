package assignment.controller;

import assignment.dto.request.OrdineRequestDTO;
import assignment.dto.response.OrdineResponseDTO;
import assignment.service.OrdineService;
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
@RequestMapping("/api/v1/ordini")
@Tag(name = "Ordini", description = "Gestione degli ordini")
public class OrdineController {

    private static final Logger log = LoggerFactory.getLogger(OrdineController.class);
    private final OrdineService ordineService;

    public OrdineController(OrdineService ordineService) {
        this.ordineService = ordineService;
    }

    @Operation(
            summary = "Ottieni lista ordini",
            description = "Recupera tutti gli ordini o una pagina specifica. Supporta il sorting tramite i parametri standard."
    )
    @GetMapping
    public ResponseEntity<?> getListaOrdini(@ParameterObject Pageable pageable,
                                            @RequestParam(required = false) Integer page){
        if(page==null) {
            log.info("Richiesta lista completa ordini");
            return ResponseEntity.ok(ordineService.getListaOrdini());
        }
        log.info("Richiesta lista ordini paginata: pagina {}, dimensione {}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(ordineService.getListaOrdini(pageable));
    }

    @Operation(
            summary = "Crea un nuovo ordine",
            description = "Crea un ordine per un cliente esistente e scala lo stock dei prodotti. Restituisce 400 se lo stock è insufficiente.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Ordine creato con successo"),
                    @ApiResponse(responseCode = "400", description = "Dati non validi o stock insufficiente"),
                    @ApiResponse(responseCode = "404", description = "Cliente o prodotto non trovato")
            }
    )
    @PostMapping
    public ResponseEntity<OrdineResponseDTO> aggiungiOrdine(@Valid @RequestBody OrdineRequestDTO ordine) {
        log.info("Ricevuta richiesta creazione ordine per cliente: {}", ordine.codFiscale());
        OrdineResponseDTO nuovoOrdine = ordineService.aggiungiOrdine(ordine);
        log.info("Ordine creato con successo. ID assegnato: {}", nuovoOrdine.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuovoOrdine);
    }

    @Operation(
            summary = "Cancella un ordine",
            description = "Porta l'ordine in stato CANCELLATO e ripristina lo stock. Disponibile solo se l'ordine non è già consegnato.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ordine cancellato"),
                    @ApiResponse(responseCode = "400", description = "L'ordine non è in uno stato cancellabile"),
                    @ApiResponse(responseCode = "404", description = "Ordine non trovato")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<OrdineResponseDTO> deleteOrdine(@PathVariable Long id) {
        log.warn("Richiesta eliminazione ordine ID: {}", id);
        OrdineResponseDTO eliminato = ordineService.deleteOrdine(id);
        log.info("Ordine ID {} eliminato correttamente", id);
        return ResponseEntity.ok(eliminato);
    }

    @Operation(
            summary = "Avanza lo stato dell'ordine",
            description = "Sposta l'ordine allo stato successivo (es. da ORDINATO a SPEDITO).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stato aggiornato con successo"),
                    @ApiResponse(responseCode = "400", description = "L'ordine ha già raggiunto lo stato finale")
            }
    )
    @PatchMapping("/{id}/stato")
    public ResponseEntity<OrdineResponseDTO> avanzaStato(@PathVariable Long id) {
        log.info("Richiesta avanzamento stato per ordine ID: {}", id);
        OrdineResponseDTO aggiornato = ordineService.cambiaStato(id);
        log.info("Stato ordine ID {} aggiornato a: {}", id, aggiornato.stato());
        return ResponseEntity.ok(aggiornato);
    }

    @Operation(
            summary = "Ricerca un ordine per identificativo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ordine trovato"),
                    @ApiResponse(responseCode = "404", description = "Ordine non trovato", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrdineResponseDTO> getOrdine(@PathVariable Long id) {
        log.debug("Recupero dettaglio ordine ID: {}", id);
        return ResponseEntity.ok(ordineService.getOrdine(id));
    }
}
