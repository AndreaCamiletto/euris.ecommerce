package assignment.service;

import assignment.models.Cliente;
import assignment.models.Ordine;
import assignment.models.Prodotto;

import java.util.List;

public interface EcommerceInterface {

    boolean aggiungiCliente(Cliente cliente);
    List<Cliente> getClienti();
    boolean aggiungiProdotto(Prodotto prodotto);
    List<Prodotto> getProdotti();
    boolean aggiungiOrdine(Ordine ordine);
    List<Ordine> getOrdini();

}
