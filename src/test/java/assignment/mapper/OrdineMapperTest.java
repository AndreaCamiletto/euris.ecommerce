package assignment.mapper;

import assignment.dto.request.OrdineProdottoRequestDTO;
import assignment.dto.request.OrdineRequestDTO;
import assignment.dto.response.OrdineProdottoResponseDTO;
import assignment.dto.response.OrdineResponseDTO;
import assignment.exceptions.ProdottoNonTrovatoException;
import assignment.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdineMapperTest {

    @Mock
    private OrdineProdottoMapper ordineProdottoMapper;

    @InjectMocks
    private OrdineMapper ordineMapper;

    @Test
    void toEntitySuccesso() {
        Cliente cliente = new Cliente();
        cliente.setCodFiscale("CF123");

        Prodotto p1 = new Prodotto();
        p1.setCodProdotto("P1");

        OrdineProdottoRequestDTO itemDto = new OrdineProdottoRequestDTO("P1", 2);
        OrdineRequestDTO request = new OrdineRequestDTO("CF123", List.of(itemDto));

        Map<String, Prodotto> mappaProdotti = Map.of("P1", p1);

        when(ordineProdottoMapper.toEntity(any(), any())).thenReturn(new OrdineProdotto());

        Ordine ordine = ordineMapper.toEntity(request, cliente, mappaProdotti);

        assertNotNull(ordine);
        assertEquals(cliente, ordine.getCliente());
    }

    @Test
    public void toEntity_Null_RitornaNull() {
        assertNull(ordineMapper.toEntity(null, null, null));
    }

    @Test
    public void toEntityProdottoNonTrovatoLanciaException() {
        OrdineRequestDTO request = new OrdineRequestDTO("CF", List.of(new OrdineProdottoRequestDTO("P_ERRATO", 1)));
        Map<String, Prodotto> mappaVuota = Map.of();

        assertThrows(ProdottoNonTrovatoException.class, () ->
                ordineMapper.toEntity(request, new Cliente(), mappaVuota)
        );
    }

    @Test
    public void toResponseDTOSuccesso() {
        Cliente cliente = new Cliente();
        cliente.setCodFiscale("CF123");

        Ordine ordine = new Ordine();
        ordine.setCliente(cliente);

        OrdineProdotto op = new OrdineProdotto();
        ordine.addProdotto(op);

        when(ordineProdottoMapper.toResponseDTO(any())).thenReturn(new OrdineProdottoResponseDTO("P1", 2));

        OrdineResponseDTO response = ordineMapper.toResponseDTO(ordine);

        assertNotNull(response);
        assertEquals("CF123", response.codFiscale());
        assertEquals(1, response.prodottiOrdine().size());
    }

    @Test
    public void toResponseDTO_Null_RitornaNull() {
        assertNull(ordineMapper.toResponseDTO(null));
    }
}