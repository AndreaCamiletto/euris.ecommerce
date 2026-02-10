package assignment.dto.response;

import java.util.List;

public record OrdineResponseDTO(
        Long id,
        String codFiscale,
        List<OrdineProdottoResponseDTO> prodottiOrdine,
        String stato,
        Long version
) {
}
