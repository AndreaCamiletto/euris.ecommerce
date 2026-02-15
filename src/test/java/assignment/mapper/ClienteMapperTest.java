package assignment.mapper;

import assignment.dto.request.ClienteRequestDTO;
import assignment.dto.response.ClienteResponseDTO;
import assignment.models.Cliente;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ClienteMapperTest {

    private final ClienteMapper mapper = new ClienteMapper();

    @Test
    public void toEntitySuccesso() {
        ClienteRequestDTO dto = new ClienteRequestDTO(
                "RSSMRA80A01H501U", "Mario", "Rossi", "mario@email.com", LocalDate.of(1980, 1, 1)
        );

        Cliente entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.codFiscale(), entity.getCodFiscale());
        assertEquals(dto.nome(), entity.getNome());
        assertEquals(dto.email(), entity.getEmail());
    }

    @Test
    public void toEntityNullRitornaNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    public void toResponseDTOSuccesso() {
        Cliente cliente = new Cliente();
        cliente.setCodFiscale("RSSMRA80A01H501U");
        cliente.setNome("Mario");
        cliente.setCognome("Rossi");
        cliente.setEmail("mario@email.com");
        cliente.setDataNascita(LocalDate.of(1980, 1, 1));

        ClienteResponseDTO dto = mapper.toResponseDTO(cliente);

        assertNotNull(dto);
        assertEquals(cliente.getCodFiscale(), dto.codFiscale());
        assertEquals(cliente.getNome(), dto.nome());
    }

    @Test
    public void toResponseDTONullRitornaNull() {
        assertNull(mapper.toResponseDTO(null));
    }
}