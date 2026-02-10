package assignment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ClienteRequestDTO(
        @NotBlank String codFiscale,
        @NotNull String nome,
        @NotNull String cognome,
        @NotBlank @Email String email,
        @NotNull LocalDate dataNascita
) {
}
