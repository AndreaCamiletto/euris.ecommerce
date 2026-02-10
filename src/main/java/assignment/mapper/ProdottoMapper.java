package assignment.mapper;

import assignment.dto.request.ProdottoRequestDTO;
import assignment.dto.response.ProdottoResponseDTO;
import assignment.models.Prodotto;
import org.springframework.stereotype.Component;

@Component
public class ProdottoMapper {

    public Prodotto toEntity(ProdottoRequestDTO request) {
        if(request == null) {
            return null;
        }
        Prodotto prodotto = new Prodotto();
        prodotto.setCodProdotto(request.codProdotto());
        prodotto.setNome(request.nome());
        prodotto.setStock(request.stock());
        return prodotto;
    }

    public ProdottoResponseDTO toReponseDTO(Prodotto prodotto) {
        if(prodotto == null) {
            return null;
        }
        return new ProdottoResponseDTO(
                prodotto.getCodProdotto(),
                prodotto.getNome(),
                prodotto.getStock(),
                prodotto.getVersion()
        );
    }
}
