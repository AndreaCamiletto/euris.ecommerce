package assignment.controller;

import assignment.dto.request.ClienteRequestDTO;
import assignment.dto.response.ClienteResponseDTO;
import assignment.service.ClienteService;
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
@RequestMapping("/api/v1/clienti")
@Tag(name = "Clienti", description = "Gestione delle anagrafiche clienti")
public class ClienteController {

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(
            summary = "Recupera la lista dei clienti",
            description = "Restituisce tutti i clienti.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista recuperata con successo")
            }
    )
    @GetMapping
    public ResponseEntity<?> getListaClienti(@ParameterObject Pageable pageable,
                                             @RequestParam(required = false) Integer page) {
        if (page == null) {
            log.info("Richiesta lista completa clienti (senza paginazione)");
            return ResponseEntity.ok(clienteService.getListaClienti());
        }
        log.info("Richiesta lista clienti paginata: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(clienteService.getListaClienti(pageable));
    }

    @Operation(
            summary = "Crea un nuovo cliente",
            description = "Inserisce un nuovo cliente nel sistema. Restituisce 409 se il codice fiscale è già presente.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cliente creato con successo"),
                    @ApiResponse(responseCode = "400", description = "Dati di input non validi"),
                    @ApiResponse(responseCode = "409", description = "Cliente già esistente, Codice Fiscale duplicato)")
            }
    )
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> aggiungiCliente(@Valid @RequestBody ClienteRequestDTO cliente) {
        log.info("Richiesta creazione nuovo cliente: {}", cliente.codFiscale());
        ClienteResponseDTO response = clienteService.aggiungiCliente(cliente);
        log.info("Cliente creato con successo: {}", response.codFiscale());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(
            summary = "Ricerca un cliente per Codice Fiscale",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente trovato"),
                    @ApiResponse(responseCode = "404", description = "Cliente non trovato", content = @Content)
            }
    )
    @GetMapping("/{codFiscale}")
    public ResponseEntity<ClienteResponseDTO> getCliente(@PathVariable String codFiscale) {
        log.info("Ricerca cliente con codice fiscale: {}", codFiscale);
        return ResponseEntity.ok(clienteService.getCliente(codFiscale));
    }

}
