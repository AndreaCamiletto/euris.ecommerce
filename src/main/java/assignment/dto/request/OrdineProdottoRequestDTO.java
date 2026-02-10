package assignment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record OrdineProdottoRequestDTO(
        @NotBlank String codProdotto,
        @Min(1) Integer quantita
) {
}
