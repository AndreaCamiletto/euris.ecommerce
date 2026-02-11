package assignment.service;

import assignment.dto.request.ClienteRequestDTO;
import assignment.dto.request.OrdineProdottoRequestDTO;
import assignment.dto.request.OrdineRequestDTO;
import assignment.dto.request.ProdottoRequestDTO;
import assignment.dto.response.ClienteResponseDTO;
import assignment.dto.response.OrdineResponseDTO;
import assignment.dto.response.ProdottoResponseDTO;
import assignment.exceptions.*;
import assignment.mapper.ClienteMapper;
import assignment.mapper.OrdineMapper;
import assignment.mapper.ProdottoMapper;
import assignment.models.Cliente;
import assignment.models.Ordine;
import assignment.models.OrdineProdotto;
import assignment.models.Prodotto;
import assignment.repository.ClienteRepository;
import assignment.repository.OrdineRepository;
import assignment.repository.ProdottoRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Ecommerce implements EcommerceInterface {

    private final ClienteRepository clienteRepository;
    private final ProdottoRepository prodottoRepository;
    private final OrdineRepository ordineRepository;
    private final ClienteMapper clienteMapper;
    private final ProdottoMapper prodottoMapper;
    private final OrdineMapper ordineMapper;

    public Ecommerce(ClienteRepository clienteRepository,
                     ProdottoRepository prodottoRepository,
                     OrdineRepository ordineRepository,
                     ClienteMapper clienteMapper,
                     ProdottoMapper prodottoMapper, OrdineMapper ordineMapper) {
        this.clienteRepository = clienteRepository;
        this.prodottoRepository = prodottoRepository;
        this.ordineRepository = ordineRepository;
        this.clienteMapper = clienteMapper;
        this.prodottoMapper = prodottoMapper;
        this.ordineMapper = ordineMapper;
    }

    @Override
    public ClienteResponseDTO aggiungiCliente(ClienteRequestDTO clienteRequest){
        Cliente cliente = clienteMapper.toEntity(clienteRequest);
        if(cliente == null) {
            throw new IllegalArgumentException("Impossibile inserire un cliente null");
        }
        if(clienteRepository.existsById(cliente.getCodFiscale())) {
            throw new ClienteDuplicatoException("Cliente già presente: " + cliente.getCodFiscale());
        }
        return clienteMapper.toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    public List<ClienteResponseDTO> getClienti() {
        List<Cliente> clienti = clienteRepository.findAll();
        List<ClienteResponseDTO> clientiResponse = new ArrayList<>();
        for(Cliente cliente : clienti) {
            ClienteResponseDTO clienteResponseDTO = clienteMapper.toResponseDTO(cliente);
            clientiResponse.add(clienteResponseDTO);
        }
        return clientiResponse;
    }

    @Override
    public Page<ClienteResponseDTO> getClientiPaginati(Pageable pageable) {
        return clienteRepository.findAll(pageable).map(clienteMapper::toResponseDTO);
    }

    @Override
    public ProdottoResponseDTO aggiungiProdotto(ProdottoRequestDTO prodottoRequest) {
        Prodotto prodotto = prodottoMapper.toEntity(prodottoRequest);
        if(prodotto == null) {
            throw new IllegalArgumentException("Impossibile inserire un prodotto null");
        }
        if(prodottoRepository.existsById(prodotto.getCodProdotto())) {
            throw new ProdottoDuplicatoException("Prodotto già presente: " + prodotto.getCodProdotto());
        }
        return prodottoMapper.toReponseDTO(prodottoRepository.save(prodotto));
    }

    @Override
    public List<ProdottoResponseDTO> getProdotti() {
        List<Prodotto> prodotti = prodottoRepository.findAll();
        List<ProdottoResponseDTO> prodottiResponse = new ArrayList<>();
        for(Prodotto prodotto : prodotti) {
            prodottiResponse.add(prodottoMapper.toReponseDTO(prodotto));
        }
        return prodottiResponse;
    }

    @Override
    public Page<ProdottoResponseDTO> getProdottiPaginati(Pageable pageable) {
        return prodottoRepository.findAll(pageable).map(prodottoMapper::toReponseDTO);
    }

    @Override
    @Retryable(
            value = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50)
    )
    @Transactional
    public OrdineResponseDTO aggiungiOrdine(OrdineRequestDTO ordineRequest) {
        Cliente cliente = clienteRepository.findById(ordineRequest.codFiscale()).
                orElseThrow(() -> new IllegalArgumentException("Cliente non trovato"));
        List<String> codiciProdotto = new ArrayList<>();
        for(OrdineProdottoRequestDTO ordineProdottoRequestDTO : ordineRequest.prodottiOrdine()) {
                codiciProdotto.add(ordineProdottoRequestDTO.codProdotto());
        }
        Map<String, Prodotto> mappaProdotti = getProdottiMap(prodottoRepository.findAllById(codiciProdotto));
        Ordine ordine = ordineMapper.toEntity(ordineRequest, cliente,mappaProdotti);
        aggiornaStockOrdine(ordine.getOrdineProdotto(),-1);
        return ordineMapper.toResponseDTO(ordineRepository.save(ordine));
    }

    @Override
    public List<OrdineResponseDTO> getOrdini() {
        List<Ordine> ordini = ordineRepository.findAll();
        List<OrdineResponseDTO> ordiniResponse = new ArrayList<>();
        for(Ordine ordine : ordini) {
            ordiniResponse.add(ordineMapper.toResponseDTO(ordine));
        }
        return ordiniResponse;
    }

    @Override
    public Page<OrdineResponseDTO> getOrdiniPaginati(Pageable pageable) {
        return ordineRepository.findAll(pageable).map(ordineMapper::toResponseDTO);
    }

    @Retryable(
            value = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50)
    )
    @Transactional
    public OrdineResponseDTO deleteOrdine(Long id) {
        Ordine ordine = ordineRepository.findById(id).
                orElseThrow(() -> new OrdineNonTrovatoException("Non è presente l'ordine avente l'id indicato"));
        if (!ordine.cancellabile()) {
            throw new OrdineNonCancellabileException("Non è possibile cancellare l'ordine in stato " + ordine.getStato());
        }
        ordine.avanzaStatoCancellato();
        Ordine ordineSalvato = ordineRepository.save(ordine);
        aggiornaStockOrdine(ordineSalvato.getOrdineProdotto(), 1);
        return ordineMapper.toResponseDTO(ordineSalvato);
    }

    @Retryable(
            value = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50)
    )
    @Transactional
    public OrdineResponseDTO cambiaStato(Long id) {
        Ordine ordine = ordineRepository.findById(id).
                orElseThrow(() -> new OrdineNonTrovatoException("Non è presente l'ordine avente l'id indicato"));
        if(!ordine.avanzabile()) {
            throw new OrdineNonAvanzabileException("L'ordine non può avanzare dallo stato " + ordine.getStato());
        }
        ordine.avanzaStato();
        Ordine ordineSalvato = ordineRepository.save(ordine);
        return ordineMapper.toResponseDTO(ordineSalvato);
    }

    private void aggiornaStockOrdine(List<OrdineProdotto> prodottiOrdine, Integer moltiplicatore) {
        List<String> codiciProdotto = getCodiciProdotto(prodottiOrdine);
        List<Prodotto> prodottiSalvati = prodottoRepository.findAllById(codiciProdotto);
        Map<String, Prodotto> mapProdotti = getProdottiMap(prodottiSalvati);
        for (OrdineProdotto prodottoOrdine : prodottiOrdine) {
            Prodotto prodottoCatalogo = mapProdotti.get(prodottoOrdine.getProdotto().getCodProdotto());
            if (prodottoCatalogo == null) {
                throw new ProdottoNonTrovatoException("Prodotto non presente: " + prodottoOrdine.getProdotto().getCodProdotto());
            }
            if (moltiplicatore < 0 && prodottoCatalogo.getStock() < prodottoOrdine.getQuantita()) {
                throw new ProdottoOutOfStockException(
                        "Impossibile effettuare ordine, quantità presente inferiore a quantità richiesta: "
                                + prodottoOrdine.getProdotto().getCodProdotto()
                );
            }
            prodottoCatalogo.setStock(prodottoCatalogo.getStock() + moltiplicatore * prodottoOrdine.getQuantita());
            prodottoOrdine.setProdotto(prodottoCatalogo);
        }
        prodottoRepository.saveAll(mapProdotti.values());
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
