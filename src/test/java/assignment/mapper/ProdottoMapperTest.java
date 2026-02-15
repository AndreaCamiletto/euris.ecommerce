package assignment.mapper;

import assignment.dto.request.ProdottoRequestDTO;
import assignment.dto.response.ProdottoResponseDTO;
import assignment.models.Prodotto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProdottoMapperTest {

    private final ProdottoMapper mapper = new ProdottoMapper();

    @Test
    public void toEntitySuccesso() {
        ProdottoRequestDTO dto = new ProdottoRequestDTO("P001", "Smartphone", 10);

        Prodotto entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.codProdotto(), entity.getCodProdotto());
        assertEquals(dto.nome(), entity.getNome());
        assertEquals(dto.stock(), entity.getStock());
    }

    @Test
    public void toEntityNullRitornaNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    public void toResponseDTOSuccesso() {
        Prodotto prodotto = new Prodotto();
        prodotto.setCodProdotto("P001");
        prodotto.setNome("Smartphone");
        prodotto.setStock(10);

        ProdottoResponseDTO dto = mapper.toResponseDTO(prodotto);

        assertNotNull(dto);
        assertEquals(prodotto.getCodProdotto(), dto.codProdotto());
        assertEquals(prodotto.getNome(), dto.nome());
        assertEquals(prodotto.getStock(), dto.stock());
    }

    @Test
    public void toResponseDTONullRitornaNull() {
        assertNull(mapper.toResponseDTO(null));
    }
}