package assignment.service;

import assignment.dto.request.ProdottoRequestDTO;
import assignment.dto.response.ProdottoResponseDTO;
import assignment.exceptions.ProdottoDuplicatoException;
import assignment.mapper.ProdottoMapper;
import assignment.models.Prodotto;
import assignment.repository.ProdottoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdottoServiceTest {

    @Mock
    private ProdottoRepository prodottoRepository;

    @Mock
    private ProdottoMapper prodottoMapper;

    @InjectMocks
    private ProdottoService prodottoService;

    @Test
    public void aggiungiProdottoSuccesso() {
        String cod = "P100";
        ProdottoRequestDTO request = new ProdottoRequestDTO(cod, "Prodotto Test", 10);
        Prodotto entity = new Prodotto();
        ProdottoResponseDTO expected = new ProdottoResponseDTO(cod, "Prodotto Test", 10);

        when(prodottoRepository.existsById(cod)).thenReturn(false);
        when(prodottoMapper.toEntity(request)).thenReturn(entity);
        when(prodottoRepository.save(entity)).thenReturn(entity);
        when(prodottoMapper.toResponseDTO(entity)).thenReturn(expected);

        ProdottoResponseDTO result = prodottoService.aggiungiProdotto(request);

        assertNotNull(result);
        assertEquals(cod, result.codProdotto());
        verify(prodottoRepository).save(any());
    }

    @Test
    public void aggiungiProdottoLanciaExceptionSeDuplicato() {
        String cod = "P100";
        ProdottoRequestDTO request = new ProdottoRequestDTO(cod, "Prodotto Test", 10);
        when(prodottoRepository.existsById(cod)).thenReturn(true);

        assertThrows(ProdottoDuplicatoException.class, () -> prodottoService.aggiungiProdotto(request));
        verify(prodottoRepository, never()).save(any());
    }

    @Test
    public void getProdottoSuccesso() {
        String cod = "P100";
        Prodotto entity = new Prodotto();
        ProdottoResponseDTO response = new ProdottoResponseDTO(cod, "Prodotto Test", 10);

        when(prodottoRepository.findById(cod)).thenReturn(Optional.of(entity));
        when(prodottoMapper.toResponseDTO(entity)).thenReturn(response);

        ProdottoResponseDTO result = prodottoService.getProdotto(cod);

        assertEquals(cod, result.codProdotto());
    }

    @Test
    public void getListaProdottiSenzaPaginazioneSuccesso() {
        Prodotto p1 = new Prodotto();
        p1.setCodProdotto("P1");
        Prodotto p2 = new Prodotto();
        p2.setCodProdotto("P2");

        List<Prodotto> entities = List.of(p1, p2);
        ProdottoResponseDTO res1 = new ProdottoResponseDTO("P1", "N1", 10);
        ProdottoResponseDTO res2 = new ProdottoResponseDTO("P2", "N2", 20);

        when(prodottoRepository.findAll()).thenReturn(entities);
        when(prodottoMapper.toResponseDTO(p1)).thenReturn(res1);
        when(prodottoMapper.toResponseDTO(p2)).thenReturn(res2);

        List<ProdottoResponseDTO> result = prodottoService.getListaProdotti();

        assertEquals(2, result.size());
        assertEquals("P1", result.get(0).codProdotto());
        assertEquals("P2", result.get(1).codProdotto());
    }

    @Test
    public void getListaProdottiPaginataSuccesso() {
        Pageable pageable = PageRequest.of(0, 5);
        Prodotto p1 = new Prodotto();
        p1.setCodProdotto("P1");
        Page<Prodotto> page = new PageImpl<>(List.of(p1));

        ProdottoResponseDTO res1 = new ProdottoResponseDTO("P1", "N1", 10);

        when(prodottoRepository.findAll(pageable)).thenReturn(page);
        when(prodottoMapper.toResponseDTO(p1)).thenReturn(res1);

        Page<ProdottoResponseDTO> result = prodottoService.getListaProdotti(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("P1", result.getContent().get(0).codProdotto());
    }
}
