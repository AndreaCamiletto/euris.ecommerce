package assignment.service;

import assignment.models.Cliente;
import assignment.models.Ordine;
import assignment.models.Prodotto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EcommerceInterface {

    boolean aggiungiCliente(Cliente cliente);
    List<Cliente> getClienti();
    Page<Cliente> getClientiPaginati(Pageable pageable);
    boolean aggiungiProdotto(Prodotto prodotto);
    List<Prodotto> getProdotti();
    Page<Prodotto> getProdottiPaginati(Pageable pageable);
    boolean aggiungiOrdine(Ordine ordine);
    List<Ordine> getOrdini();
    Page<Ordine> getOrdiniPaginati(Pageable pageable);

}
