package assignment.dto.response;

public record ProdottoResponseDTO(
        String codProdotto,
        String nome,
        Integer stock,
        Long version
) {
}
