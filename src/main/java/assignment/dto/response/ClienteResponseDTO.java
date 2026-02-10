package assignment.dto.response;

import java.time.LocalDate;

public record ClienteResponseDTO(
        String codFiscale,
        String nome,
        String cognome,
        String email,
        LocalDate dataNascita
) {
}
