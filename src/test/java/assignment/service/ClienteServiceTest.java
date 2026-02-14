package assignment.service;

import assignment.dto.request.ClienteRequestDTO;
import assignment.dto.response.ClienteResponseDTO;
import assignment.exceptions.ClienteDuplicatoException;
import assignment.exceptions.ClienteNonTrovatoException;
import assignment.mapper.ClienteMapper;
import assignment.models.Cliente;
import assignment.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Abilita l'uso di Mockito
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    public void getListaClientiSenzaPaginazioneSuccesso() {
        Cliente c1 = new Cliente("CF1");
        Cliente c2 = new Cliente("CF2");

        List<Cliente> listaEntities = List.of(c1, c2);

        ClienteResponseDTO res1 = new ClienteResponseDTO("CF1", "Nome1", "Cognome1", "mario@example.com", LocalDate.of(2025,9,12));
        ClienteResponseDTO res2 = new ClienteResponseDTO("CF2", "Nome2", "Cognome2", "mario@example.com", LocalDate.of(2025,9,12));

        when(clienteRepository.findAll()).thenReturn(listaEntities);
        when(clienteMapper.toResponseDTO(c1)).thenReturn(res1);
        when(clienteMapper.toResponseDTO(c2)).thenReturn(res2);

        List<ClienteResponseDTO> result = clienteService.getListaClienti();

        assertEquals(2, result.size());
        assertEquals("CF1", result.get(0).codFiscale());
        assertEquals("CF2", result.get(1).codFiscale());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    public void getListaClientiConPaginazioneSuccesso() {
        Pageable pageable = PageRequest.of(0, 10);
        Cliente c1 = new Cliente();
        c1.setCodFiscale("CF1");
        Page<Cliente> paginaEntities = new PageImpl<>(List.of(c1));

        ClienteResponseDTO res1 = new ClienteResponseDTO("CF1", "Nome1", "Cognome1", "mario@example.com", LocalDate.of(2025,9,12));

        when(clienteRepository.findAll(pageable)).thenReturn(paginaEntities);
        when(clienteMapper.toResponseDTO(c1)).thenReturn(res1);

        Page<ClienteResponseDTO> result = clienteService.getListaClienti(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("CF1", result.getContent().get(0).codFiscale());
        verify(clienteRepository, times(1)).findAll(pageable);
    }

    @Test
    public void aggiungiClienteSuccesso() {
        String cf = "RSSMRA80A01H501U";
        ClienteRequestDTO request = new ClienteRequestDTO(cf, "Mario", "Rossi", "mario@example.com", LocalDate.of(2025,9,12));
        Cliente clienteEntity = new Cliente();
        ClienteResponseDTO expectedResponse = new ClienteResponseDTO(cf, "Mario", "Rossi", "mario@example.com", LocalDate.of(2025,9,12));

        when(clienteRepository.existsById(cf)).thenReturn(false);
        when(clienteMapper.toEntity(request)).thenReturn(clienteEntity);
        when(clienteRepository.save(clienteEntity)).thenReturn(clienteEntity);
        when(clienteMapper.toResponseDTO(clienteEntity)).thenReturn(expectedResponse);

        ClienteResponseDTO result = clienteService.aggiungiCliente(request);

        assertNotNull(result);
        assertEquals(cf, result.codFiscale());
        verify(clienteRepository, times(1)).save(any());
    }

    @Test
    public void aggiungiClienteErroreGiaEsistente() {
        String cf = "RSSMRA80A01H501U";
        ClienteRequestDTO request = new ClienteRequestDTO(cf, "Mario", "Rossi", "mario@example.com", LocalDate.of(2025,9,12));

        when(clienteRepository.existsById(cf)).thenReturn(true);

        assertThrows(ClienteDuplicatoException.class, () -> clienteService.aggiungiCliente(request));
        verify(clienteRepository, never()).save(any());
    }


    @Test
    public void getClienteSuccesso() {
        String cf = "RSSMRA80A01H501U";
        Cliente cliente = new Cliente();
        ClienteResponseDTO responseDTO = new ClienteResponseDTO(cf, "Mario", "Rossi", "mario@example.com", LocalDate.of(2025,9,12));

        when(clienteRepository.findById(cf)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        ClienteResponseDTO result = clienteService.getCliente(cf);

        assertNotNull(result);
        assertEquals(cf, result.codFiscale());
    }

    @Test
    public void getClienteLanciaExceptionSeNonTrovato() {
        String cf = "NON_ESISTE";
        when(clienteRepository.findById(cf)).thenReturn(Optional.empty());
        assertThrows(ClienteNonTrovatoException.class, () -> clienteService.getCliente(cf));
    }
}