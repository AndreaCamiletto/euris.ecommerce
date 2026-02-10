package assignment.mapper;

import assignment.dto.request.OrdineProdottoRequestDTO;
import assignment.dto.response.OrdineProdottoResponseDTO;
import assignment.exceptions.ProdottoNonTrovatoException;
import assignment.models.OrdineProdotto;
import assignment.models.Prodotto;
import org.springframework.stereotype.Component;

@Component
public class OrdineProdottoMapper {

    public OrdineProdotto toEntity(OrdineProdottoRequestDTO request, Prodotto prodotto) throws ProdottoNonTrovatoException {
        if(request == null) {
            return null;
        }
        OrdineProdotto ordineProdotto = new OrdineProdotto();
        ordineProdotto.setProdotto(prodotto);
        ordineProdotto.setQuantita(request.quantita());
        return ordineProdotto;
    }

    public OrdineProdottoResponseDTO toResponseDTO(OrdineProdotto ordineProdotto) {
        if(ordineProdotto == null) {
            return null;
        }
        return new OrdineProdottoResponseDTO(
                ordineProdotto.getProdotto().getCodProdotto(),
                ordineProdotto.getQuantita());
    }
}
