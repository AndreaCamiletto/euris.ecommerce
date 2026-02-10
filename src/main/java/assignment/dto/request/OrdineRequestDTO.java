package assignment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrdineRequestDTO(
        @NotBlank String codFiscale,
        @NotEmpty List<OrdineProdottoRequestDTO> prodottiOrdine
        ) {
}
