package assignment.service;

import assignment.exceptions.ClienteDuplicatoException;
import assignment.exceptions.ProdottoDuplicatoException;
import assignment.exceptions.ProdottoNonTrovatoException;
import assignment.exceptions.ProdottoOutOfStockException;
import assignment.models.Cliente;
import assignment.models.Ordine;
import assignment.models.Prodotto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class Ecommerce implements EcommerceInterface {

    private List<Cliente> clienti = new ArrayList<>();
    private List<Prodotto> prodotti = new ArrayList<>();
    private List<Ordine> ordini = new ArrayList<>();

    @Override
    public boolean aggiungiCliente(Cliente cliente){

        for(Cliente cli : clienti) {
            if(cli.equals(cliente)) {
                throw new ClienteDuplicatoException("Cliente già presente: " + cliente.getCodFiscale());
            }
        }
        return clienti.add(cliente);
    }

    @Override
    public List<Cliente> getClienti() {
        return List.copyOf(this.clienti);
    }

    @Override
    public boolean aggiungiProdotto(Prodotto prodotto) {
        for(Prodotto prod : prodotti) {
            if(prod.equals(prodotto)) {
                throw new ProdottoDuplicatoException("Prodotto già presente: " + prodotto.getCodProdotto());
            }
        }
        return prodotti.add(prodotto);
    }

    @Override
    public List<Prodotto> getProdotti() {
        return List.copyOf(prodotti);
    }

    @Override
    public boolean aggiungiOrdine(Ordine ordine) {

        for(Ordine ord : ordini){
            if(ord.equals(ordine)) {
                return false;
            }
        }
        Set<Prodotto> prodottiOrdine = ordine.getOrdine().keySet();
        aggiornaStockOrdine(prodottiOrdine, ordine);
        return ordini.add(ordine);
    }

    @Override
    public List<Ordine> getOrdini() {
        return List.copyOf(ordini);
    }

    private void aggiornaStockOrdine(Set<Prodotto> prodottiOrdine, Ordine ordine) {
        for (Prodotto prodottoOrdine : prodottiOrdine) {
            if (!prodotti.contains(prodottoOrdine)) {
                throw new ProdottoNonTrovatoException("Prodotto non presente: " + prodottoOrdine.getCodProdotto());
            }
            Prodotto prodottoCatalogo = prodotti.get(prodotti.indexOf(prodottoOrdine));
            int quantitaOrdine = ordine.getOrdine().get(prodottoOrdine);
            if (prodottoCatalogo.getStock() < quantitaOrdine) {
                throw new ProdottoOutOfStockException(
                        "Impossibile effettuare ordine, quantità presente inferiore a quantità richiesta: "
                                + prodottoOrdine.getCodProdotto()
                );
            }
            prodottoCatalogo.setStock(prodottoCatalogo.getStock() - quantitaOrdine);
        }
    }


}
