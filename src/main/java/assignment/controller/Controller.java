package assignment.controller;

import assignment.models.Cliente;
import assignment.models.Ordine;
import assignment.models.Prodotto;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    Ecommerce ecommerce;

    @GetMapping("/clienti")
    public List<Cliente> getClienti(){
        return ecommerce.getClienti();
    }


    @GetMapping("/clientiPaginati")
    public Page<Cliente> listaClienti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ecommerce.getClientiPaginati(pageable);
    }

    @PostMapping("/clienti")
    public ResponseEntity<Cliente> insertCliente(@RequestBody Cliente cliente) {
        ecommerce.aggiungiCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }



    @GetMapping("/prodotti")
    public List<Prodotto> getProdotto(){
        return ecommerce.getProdotti();
    }

    @GetMapping("/prodottiPaginati")
    public Page<Prodotto> listaProdotti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ecommerce.getProdottiPaginati(pageable);
    }

    @PostMapping("/prodotti")
    public ResponseEntity<Prodotto> insertProdotto(@RequestBody Prodotto prodotto) {
        ecommerce.aggiungiProdotto(prodotto);
        return ResponseEntity.status(HttpStatus.CREATED).body(prodotto);
    }

    @GetMapping("/ordini")
    public List<Ordine> getOrdini(){
        return ecommerce.getOrdini();
    }

    @GetMapping("/ordiniPaginati")
    public Page<Ordine> getListaOrdini(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ecommerce.getOrdiniPaginati(pageable);
    }

    @PostMapping("/ordini")
    public ResponseEntity<Ordine> insertOrdine(@RequestBody Ordine ordine) {
        ecommerce.aggiungiOrdine(ordine);
        return ResponseEntity.status(HttpStatus.CREATED).body(ordine);
    }

    @DeleteMapping("/ordini/{id}")
    public ResponseEntity<Long> deleteOrdine(@PathVariable Long id) {
        ecommerce.deleteOrdine(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(id);
    }

}
