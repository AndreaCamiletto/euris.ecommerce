package assignment.controller;

import assignment.dto.request.ClienteRequestDTO;
import assignment.dto.request.OrdineRequestDTO;
import assignment.dto.request.ProdottoRequestDTO;
import assignment.dto.response.ClienteResponseDTO;
import assignment.dto.response.OrdineResponseDTO;
import assignment.dto.response.ProdottoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import assignment.service.Ecommerce;

import java.util.List;

@RestController
public class Controller {

    private final Ecommerce ecommerce;

    public Controller(Ecommerce ecommerce) {
        this.ecommerce = ecommerce;
    }

    @GetMapping("/clienti")
    public ResponseEntity<List<ClienteResponseDTO>> getClienti(){
        return ResponseEntity.ok(ecommerce.getClienti());
    }


    @GetMapping("/clientiPaginati")
    public ResponseEntity<Page<ClienteResponseDTO>> listaClienti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ecommerce.getClientiPaginati(pageable));
    }

    @PostMapping("/clienti")
    public ResponseEntity<ClienteResponseDTO> insertCliente(@Valid @RequestBody ClienteRequestDTO cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ecommerce.aggiungiCliente(cliente));
    }

    @GetMapping("/prodotti")
    public ResponseEntity<List<ProdottoResponseDTO>> getProdotto(){
        return ResponseEntity.ok(ecommerce.getProdotti());
    }

    @GetMapping("/prodottiPaginati")
    public ResponseEntity<Page<ProdottoResponseDTO>> listaProdotti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ecommerce.getProdottiPaginati(pageable));
    }

    @PostMapping("/prodotti")
    public ResponseEntity<ProdottoResponseDTO> insertProdotto(@Valid @RequestBody ProdottoRequestDTO prodotto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ecommerce.aggiungiProdotto(prodotto));
    }

    @GetMapping("/ordini")
    public ResponseEntity<List<OrdineResponseDTO>> getOrdini(){
        return ResponseEntity.ok(ecommerce.getOrdini());
    }

    @GetMapping("/ordiniPaginati")
    public ResponseEntity<Page<OrdineResponseDTO>> getListaOrdini(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ecommerce.getOrdiniPaginati(pageable));
    }

    @PostMapping("/ordini")
    public ResponseEntity<OrdineResponseDTO> insertOrdine(@Valid @RequestBody OrdineRequestDTO ordine) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ecommerce.aggiungiOrdine(ordine));
    }

    @DeleteMapping("/ordini/{id}")
    public ResponseEntity<OrdineResponseDTO> deleteOrdine(@PathVariable Long id) {
        return ResponseEntity.ok(ecommerce.deleteOrdine(id));
    }

    @PatchMapping("/ordini/{id}/stato")
    public ResponseEntity<OrdineResponseDTO> avanzaStato(@PathVariable Long id) {
        return ResponseEntity.ok(ecommerce.cambiaStato(id));
    }

}
