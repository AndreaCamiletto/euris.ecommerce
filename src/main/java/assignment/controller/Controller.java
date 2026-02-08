package assignment.controller;

import assignment.models.Cliente;
import assignment.models.Ordine;
import assignment.models.Prodotto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping("/clienti")
    public ResponseEntity<Cliente> insertCliente(@RequestBody Cliente cliente) {
        ecommerce.aggiungiCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    @GetMapping("/prodotti")
    public List<Prodotto> getProdotto(){
        return ecommerce.getProdotti();
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

    @PostMapping("/ordini")
    public ResponseEntity<Ordine> insertOrdine(@RequestBody Ordine ordine) {
        ecommerce.aggiungiOrdine(ordine);
        return ResponseEntity.status(HttpStatus.CREATED).body(ordine);
    }

}
