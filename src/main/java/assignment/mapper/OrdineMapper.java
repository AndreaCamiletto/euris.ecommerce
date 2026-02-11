package assignment.mapper;

import assignment.dto.request.OrdineProdottoRequestDTO;
import assignment.dto.request.OrdineRequestDTO;
import assignment.dto.response.OrdineProdottoResponseDTO;
import assignment.dto.response.OrdineResponseDTO;
import assignment.exceptions.ProdottoNonTrovatoException;
import assignment.models.Cliente;
import assignment.models.Ordine;
import assignment.models.OrdineProdotto;
import assignment.models.Prodotto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OrdineMapper {

    private final OrdineProdottoMapper ordineProdottoMapper;

    public OrdineMapper(OrdineProdottoMapper ordineProdottoMapper) {
        this.ordineProdottoMapper = ordineProdottoMapper;
    }

    public Ordine toEntity(OrdineRequestDTO request, Cliente cliente, Map<String, Prodotto> prodotti) {
        if(request == null) {
            return null;
        }
        Ordine ordine = new Ordine();
        ordine.setCliente(cliente);
        List<OrdineProdotto> prodottiOrdine = new ArrayList<>();
        for(OrdineProdottoRequestDTO ordineProdottoRequestDTO : request.prodottiOrdine()) {
            Prodotto prodotto = prodotti.get(ordineProdottoRequestDTO.codProdotto());
            if (prodotto == null) {
                throw new ProdottoNonTrovatoException(
                        "Prodotto " + ordineProdottoRequestDTO.codProdotto() + " non trovato"
                );
            }
            OrdineProdotto riga = ordineProdottoMapper.toEntity(ordineProdottoRequestDTO, prodotto);
            riga.setOrdine(ordine);
            prodottiOrdine.add(riga);
        }
        ordine.setProdottiOrdine(prodottiOrdine);
        return ordine;
    }

    public OrdineResponseDTO toResponseDTO(Ordine ordine) {
        if(ordine == null) {
            return null;
        }
        List<OrdineProdottoResponseDTO> ordiniProdottoReponseDTO = new ArrayList<>();
        for(OrdineProdotto ordineProdotto : ordine.getOrdineProdotto()) {
            ordiniProdottoReponseDTO.add(ordineProdottoMapper.toResponseDTO(ordineProdotto));
        }
        return new OrdineResponseDTO(
                ordine.getId(),
                ordine.getCliente().getCodFiscale(),
                ordiniProdottoReponseDTO,
                ordine.getStato(),
                ordine.getVersion()
        );
    }
}
