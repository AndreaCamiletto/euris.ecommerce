package assignment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProdottoRequestDTO(
        @NotBlank String codProdotto,
        @NotBlank String nome,
        @Positive Integer stock
) {
}
