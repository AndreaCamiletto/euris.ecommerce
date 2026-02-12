package assignment.service;

import assignment.dto.request.OrdineProdottoRequestDTO;
import assignment.dto.request.OrdineRequestDTO;
import assignment.dto.response.OrdineResponseDTO;
import assignment.exceptions.*;
import assignment.mapper.OrdineMapper;
import assignment.models.Cliente;
import assignment.models.Ordine;
import assignment.models.OrdineProdotto;
import assignment.models.Prodotto;
import assignment.repository.ClienteRepository;
import assignment.repository.OrdineRepository;
import assignment.repository.ProdottoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrdineService {

    private final static Logger log = LoggerFactory.getLogger(OrdineService.class);
    private OrdineRepository ordineRepository;
    private OrdineMapper ordineMapper;
    private ClienteRepository clienteRepository;
    private ProdottoRepository prodottoRepository;

    public OrdineService(OrdineRepository ordineRepository, OrdineMapper ordineMapper, ClienteRepository clienteRepository, ProdottoRepository prodottoRepository) {
        this.ordineRepository = ordineRepository;
        this.ordineMapper = ordineMapper;
        this.clienteRepository = clienteRepository;
        this.prodottoRepository = prodottoRepository;
    }

    public List<OrdineResponseDTO> getListaOrdini() {
        log.debug("Richiesta lista completa ordini");
        return ordineRepository.findAll()
                .stream()
                .map(ordineMapper::toResponseDTO)
                .toList();
    }

    public Page<OrdineResponseDTO> getListaOrdini(Pageable pageable) {
        log.debug("Richiesta lista completa ordini: {}", pageable);
        return ordineRepository.findAll(pageable).map(ordineMapper::toResponseDTO);
    }

    @Transactional
    public OrdineResponseDTO aggiungiOrdine(OrdineRequestDTO ordineRequest) {
        log.info("Inizio creazione nuovo ordine per cliente: {}", ordineRequest.codFiscale());
        Cliente cliente = clienteRepository.findById(ordineRequest.codFiscale()).
                orElseThrow(() -> {
                    log.error("Creazione ordine fallita: Cliente {} non trovato", ordineRequest.codFiscale());
                    return new ClienteNonTrovatoException("Cliente " + ordineRequest.codFiscale() + "non trovato");
                });

        List<String> codiciProdotto = ordineRequest.prodottiOrdine().stream()
                .map(OrdineProdottoRequestDTO::codProdotto)
                .toList();
        Map<String, Prodotto> mappaProdotti = getProdottiMap(prodottoRepository.findAllById(codiciProdotto));
        Ordine ordine = ordineMapper.toEntity(ordineRequest, cliente,mappaProdotti);
        log.debug("Verifica e aggiornamento stock per {} prodotti", ordine.getOrdineProdotto().size());
        aggiornaStockOrdine(ordine.getOrdineProdotto(),-1);
        return ordineMapper.toResponseDTO(ordineRepository.save(ordine));
    }

    @Transactional
    public OrdineResponseDTO deleteOrdine(Long id) {
        log.info("Cancellazione ordine ID: {}", id);
        Ordine ordine = ordineRepository.findById(id).
                orElseThrow(() -> new OrdineNonTrovatoException("Non è presente l'ordine avente l'id indicato"));
        if (!ordine.cancellabile()) {
            throw new OrdineNonCancellabileException("Non è possibile cancellare l'ordine in stato " + ordine.getStato());
        }
        ordine.avanzaStatoCancellato();
        Ordine ordineSalvato = ordineRepository.save(ordine);
        log.debug("Ripristino lo stock dei prodotti per cancellazione ordine {}", id);
        aggiornaStockOrdine(ordineSalvato.getOrdineProdotto(), 1);
        return ordineMapper.toResponseDTO(ordineSalvato);
    }

    @Transactional
    public OrdineResponseDTO cambiaStato(Long id) {
        log.info("Avanzamento stato ordine ID: {}", id);
        Ordine ordine = ordineRepository.findById(id).
                orElseThrow(() -> new OrdineNonTrovatoException("Non è presente l'ordine avente l'id indicato"));
        if(!ordine.avanzabile()) {
            log.warn("Avanzamento fallito: ordine {} già in stato finale", id);
            throw new OrdineNonAvanzabileException("L'ordine non può avanzare dallo stato " + ordine.getStato());
        }
        ordine.avanzaStato();
        Ordine ordineSalvato = ordineRepository.save(ordine);
        return ordineMapper.toResponseDTO(ordineSalvato);
    }

    public OrdineResponseDTO getOrdine(Long id) {
        log.debug("Ricerca ordine per id: {}", id);
        return ordineRepository.findById(id)
                .map(ordineMapper::toResponseDTO)
                .orElseThrow(() -> new OrdineNonTrovatoException("Ordine " + id + " non trovato"));
    }

    private Map<String, Prodotto> getProdottiMap(List<Prodotto> prodotti){
        return prodotti.stream().collect(Collectors.toMap(Prodotto::getCodProdotto, p -> p));
    }

    private void aggiornaStockOrdine(List<OrdineProdotto> prodottiOrdine, Integer moltiplicatore) {
        for (OrdineProdotto prodottoOrdine : prodottiOrdine) {
            String codProdotto = prodottoOrdine.getProdotto().getCodProdotto();
            int variazione = prodottoOrdine.getQuantita() * moltiplicatore;
            log.debug("Aggiornamento stock prodotto {}: variazione {}", codProdotto, variazione);
            int righeModificate = prodottoRepository.updateStock(codProdotto, variazione);
            if (righeModificate == 0) {
                log.error("ERRORE STOCK: Prodotto {} esaurito o non trovato durante l'aggiornamento", codProdotto);
                throw new ProdottoOutOfStockException("Prodotto non disponibile o stock insufficiente: " + codProdotto);
            }
        }
    }
}
