package assignment.service;

import assignment.exceptions.*;
import assignment.models.Cliente;
import assignment.models.Ordine;
import assignment.models.OrdineProdotto;
import assignment.models.Prodotto;
import assignment.repository.ClienteRepository;
import assignment.repository.OrdineRepository;
import assignment.repository.ProdottoRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Ecommerce implements EcommerceInterface {

    private final ClienteRepository clienteRepository;
    private final ProdottoRepository prodottoRepository;
    private final OrdineRepository ordineRepository;

    public Ecommerce(ClienteRepository clienteRepository, ProdottoRepository prodottoRepository, OrdineRepository ordineRepository) {
        this.clienteRepository = clienteRepository;
        this.prodottoRepository = prodottoRepository;
        this.ordineRepository = ordineRepository;
    }

    @Override
    public boolean aggiungiCliente(Cliente cliente){
        if(clienteRepository.existsById(cliente.getCodFiscale())) {
            throw new ClienteDuplicatoException("Cliente già presente: " + cliente.getCodFiscale());
        }
        clienteRepository.save(cliente);
        return true;
    }

    @Override
    public List<Cliente> getClienti() {
        return clienteRepository.findAll();
    }

    @Override
    public Page<Cliente> getClientiPaginati(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    public boolean aggiungiProdotto(Prodotto prodotto) {
        if(prodottoRepository.existsById(prodotto.getCodProdotto())) {
            throw new ProdottoDuplicatoException("Prodotto già presente: " + prodotto.getCodProdotto());
        }
        prodottoRepository.save(prodotto);
        return true;
    }

    @Override
    public List<Prodotto> getProdotti() {
        return prodottoRepository.findAll();
    }

    @Override
    public Page<Prodotto> getProdottiPaginati(Pageable pageable) {
        return prodottoRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public boolean aggiungiOrdine(Ordine ordine) {
        aggiornaStockOrdine(ordine.getProdottiOrdine(),-1);
        ordineRepository.save(ordine);
        return true;
    }

    @Override
    public List<Ordine> getOrdini() {
        return ordineRepository.findAll();
    }

    @Override
    public Page<Ordine> getOrdiniPaginati(Pageable pageable) {
        return ordineRepository.findAll(pageable);
    }

    @Transactional
    public boolean deleteOrdine(Long id) {
        int maxRetry = 3;
        for(int tentativo = 0; tentativo < maxRetry; tentativo++) {
            try {
                Optional<Ordine> ordine = ordineRepository.findById(id);
                if (ordine.isEmpty()) {
                    throw new OrdineNonTrovatoException("Non è presente l'ordine avente l'id indicato");
                }
                if (!ordine.get().cancellabile()) {
                    throw new OrdineNonCancellabileException("Non è possibile cancellare l'ordine in stato " + ordine.get().getStato());
                }
                ordine.get().avanzaStatoCancellato();
                ordineRepository.save(ordine.get());
                aggiornaStockOrdine(ordine.get().getProdottiOrdine(), 1);
            } catch (OptimisticLockException e) {
                if (tentativo == maxRetry - 1) {
                    throw new RuntimeException(
                            "Impossibile aggiornare lo stato dell'ordine per concorrenza. Riprova più tardi.", e);
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
        }
        return true;
    }



    private void aggiornaStockOrdine(List<OrdineProdotto> prodottiOrdine, Integer moltiplicatore) {
        int maxRetry = 3;
        for (int nrTentativo = 0; nrTentativo < maxRetry; nrTentativo++) {
            try {
                List<String> codiciProdotto = getCodiciProdotto(prodottiOrdine);
                List<Prodotto> prodottiSalvati = prodottoRepository.findAllById(codiciProdotto);
                Map<String, Prodotto> mapProdotti = getProdottiMap(prodottiSalvati);
                for (OrdineProdotto prodottoOrdine : prodottiOrdine) {
                    Prodotto prodottoCatalogo = mapProdotti.get(prodottoOrdine.getProdotto().getCodProdotto());
                    if (prodottoCatalogo == null) {
                        throw new ProdottoNonTrovatoException("Prodotto non presente: " + prodottoOrdine.getProdotto().getCodProdotto());

                    }
                    if (prodottoCatalogo.getStock() < prodottoOrdine.getQuantita()) {
                        throw new ProdottoOutOfStockException(
                                "Impossibile effettuare ordine, quantità presente inferiore a quantità richiesta: "
                                        + prodottoOrdine.getProdotto().getCodProdotto()
                        );
                    }
                    prodottoCatalogo.setStock(prodottoCatalogo.getStock() + moltiplicatore * prodottoOrdine.getQuantita());
                }
                prodottoRepository.saveAll(mapProdotti.values());
            } catch (OptimisticLockException e) {
                if (nrTentativo == maxRetry) {
                    throw new RuntimeException("Ordine non evadibile per concorrenza. Riprova più tardi.", e);
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private List<String> getCodiciProdotto(List<OrdineProdotto> prodottiOrdine) {
        List<String> codiciProdotto = new ArrayList<>();
        for(OrdineProdotto prodottoOrdine : prodottiOrdine) {
            codiciProdotto.add(prodottoOrdine.getProdotto().getCodProdotto());
        }
        return codiciProdotto;
    }

    private Map<String, Prodotto> getProdottiMap(List<Prodotto> prodotti){
        Map<String,Prodotto> prodottiMap = new HashMap<>();
        for(Prodotto prodotto : prodotti) {
            prodottiMap.put(prodotto.getCodProdotto(),prodotto);
        }
        return prodottiMap;
    }



}
