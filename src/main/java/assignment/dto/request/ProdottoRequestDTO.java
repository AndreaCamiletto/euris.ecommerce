package assignment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ProdottoRequestDTO(
        @NotBlank String codProdotto,
        @NotBlank String nome,
        @PositiveOrZero Integer stock
) {
}
