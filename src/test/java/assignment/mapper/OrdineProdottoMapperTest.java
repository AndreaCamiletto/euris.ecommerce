package assignment.mapper;

import assignment.dto.request.OrdineProdottoRequestDTO;
import assignment.dto.response.OrdineProdottoResponseDTO;
import assignment.exceptions.ProdottoNonTrovatoException;
import assignment.models.OrdineProdotto;
import assignment.models.Prodotto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrdineProdottoMapperTest {

    private final OrdineProdottoMapper mapper = new OrdineProdottoMapper();

    @Test
    public void toEntity_Successo() throws ProdottoNonTrovatoException {
        OrdineProdottoRequestDTO dto = new OrdineProdottoRequestDTO("P001", 5);
        Prodotto prodotto = new Prodotto();
        prodotto.setCodProdotto("P001");

        OrdineProdotto entity = mapper.toEntity(dto, prodotto);

        assertNotNull(entity);
        assertEquals(prodotto, entity.getProdotto());
        assertEquals(5, entity.getQuantita());
    }

    @Test
    public void toEntityNullRequestRitornaNull() throws ProdottoNonTrovatoException {
        assertNull(mapper.toEntity(null, new Prodotto()));
    }

    @Test
    public void toResponseDTOSuccesso() {
        Prodotto prodotto = new Prodotto();
        prodotto.setCodProdotto("P001");

        OrdineProdotto op = new OrdineProdotto();
        op.setProdotto(prodotto);
        op.setQuantita(10);

        OrdineProdottoResponseDTO dto = mapper.toResponseDTO(op);

        assertNotNull(dto);
        assertEquals("P001", dto.codProdotto());
        assertEquals(10, dto.quantita());
    }

    @Test
    public void toResponseDTONullRitornaNull() {
        assertNull(mapper.toResponseDTO(null));
    }
}