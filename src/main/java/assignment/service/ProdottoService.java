package assignment.service;

import assignment.dto.request.ProdottoRequestDTO;
import assignment.dto.response.ProdottoResponseDTO;
import assignment.exceptions.ProdottoDuplicatoException;
import assignment.exceptions.ProdottoNonTrovatoException;
import assignment.mapper.ProdottoMapper;
import assignment.repository.ProdottoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdottoService {

    private final static Logger log = LoggerFactory.getLogger(ProdottoService.class);
    private final ProdottoRepository prodottoRepository;
    private final ProdottoMapper prodottoMapper;

    public ProdottoService(ProdottoRepository prodottoRepository, ProdottoMapper prodottoMapper) {
        this.prodottoRepository = prodottoRepository;
        this.prodottoMapper = prodottoMapper;
    }

    public List<ProdottoResponseDTO> getListaProdotti() {
        log.debug("Recupero lista completa prodotti");
        return prodottoRepository.findAll()
                .stream()
                .map(prodottoMapper::toResponseDTO)
                .toList();
    }

    public Page<ProdottoResponseDTO> getListaProdotti(Pageable pageable) {
        log.debug("Recupero lista completa prodotti {}: ", pageable);
        return prodottoRepository.findAll(pageable)
                .map(prodottoMapper::toResponseDTO);
    }

    public ProdottoResponseDTO aggiungiProdotto(ProdottoRequestDTO prodottoRequest) {
        log.info("Tentativo di aggiunta nuovo prodotto: {}", prodottoRequest.codProdotto());
        if (prodottoRepository.existsById(prodottoRequest.codProdotto())) {
            log.warn("Impossibile aggiungere prodotto: codice {} già esistente", prodottoRequest.codProdotto());
            throw new ProdottoDuplicatoException("Prodotto già presente: " + prodottoRequest.codProdotto());
        }
        try {
            return prodottoMapper.toResponseDTO(prodottoRepository.save(prodottoMapper.toEntity(prodottoRequest)));

        } catch (DataIntegrityViolationException e) {
            throw new ProdottoDuplicatoException("Cliente già presente: " + prodottoRequest.codProdotto());
        }
    }
    public ProdottoResponseDTO getProdotto(String codProdotto) {
        log.debug("Ricerca prodotto con codice: {}", codProdotto);
        return prodottoRepository.findById(codProdotto)
                .map(prodottoMapper::toResponseDTO)
                .orElseThrow(()->new ProdottoNonTrovatoException("Prodotto " + codProdotto + " non trovato"));
    }
}
