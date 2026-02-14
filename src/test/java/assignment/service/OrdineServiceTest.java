package assignment.service;

import assignment.dto.request.OrdineProdottoRequestDTO;
import assignment.dto.request.OrdineRequestDTO;
import assignment.dto.response.OrdineResponseDTO;
import assignment.exceptions.*;
import assignment.mapper.OrdineMapper;
import assignment.models.Cliente;
import assignment.models.Ordine;
import assignment.models.OrdineProdotto;
import assignment.models.Prodotto;
import assignment.repository.ClienteRepository;
import assignment.repository.OrdineRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdineServiceTest {

    @Mock
    private OrdineRepository ordineRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ProdottoRepository prodottoRepository;
    @Mock
    private OrdineMapper ordineMapper;

    @InjectMocks
    private OrdineService ordineService;

    @Test
    public void aggiungiOrdineSuccesso() {
        String cf = "RSSMRA80A01H501U";
        OrdineProdottoRequestDTO prodReq = new OrdineProdottoRequestDTO("P1", 2);
        OrdineRequestDTO request = new OrdineRequestDTO(cf, List.of(prodReq));

        Cliente cliente = new Cliente();
        Prodotto prodotto = new Prodotto();
        prodotto.setCodProdotto("P1");

        Ordine ordineEntity = new Ordine();
        OrdineProdotto op = new OrdineProdotto();
        op.setProdotto(prodotto);
        op.setQuantita(2);
        ordineEntity.addProdotto(op);

        when(clienteRepository.findById(cf)).thenReturn(Optional.of(cliente));
        when(prodottoRepository.findAllById(any())).thenReturn(List.of(prodotto));
        when(ordineMapper.toEntity(eq(request), eq(cliente), anyMap())).thenReturn(ordineEntity);

        when(prodottoRepository.updateStock("P1", -2)).thenReturn(1);
        when(ordineRepository.save(any())).thenReturn(ordineEntity);
        when(ordineMapper.toResponseDTO(any())).thenReturn(new OrdineResponseDTO(1L, cf, null, "NUOVO"));

        OrdineResponseDTO result = ordineService.aggiungiOrdine(request);

        assertNotNull(result);
        verify(prodottoRepository).updateStock("P1", -2);
        verify(ordineRepository).save(any());
    }

    @Test
    public void aggiungiOrdineLanciaExceptionSeStockInsufficiente() {
        String cf = "RSSMRA80A01H501U";
        OrdineRequestDTO request = new OrdineRequestDTO(cf, List.of(new OrdineProdottoRequestDTO("P1", 100)));

        Cliente cliente = new Cliente();
        Prodotto prodotto = new Prodotto();
        prodotto.setCodProdotto("P1");

        Ordine ordineEntity = new Ordine();
        OrdineProdotto op = new OrdineProdotto();
        op.setProdotto(prodotto);
        op.setQuantita(100);
        ordineEntity.addProdotto(op);

        when(clienteRepository.findById(cf)).thenReturn(Optional.of(cliente));
        when(prodottoRepository.findAllById(any())).thenReturn(List.of(prodotto));
        when(ordineMapper.toEntity(any(), any(), any())).thenReturn(ordineEntity);

        when(prodottoRepository.updateStock("P1", -100)).thenReturn(0);

        assertThrows(ProdottoOutOfStockException.class, () -> ordineService.aggiungiOrdine(request));
        verify(ordineRepository, never()).save(any());
    }

    @Test
    public void deleteOrdineSuccesso() {
        Long id = 1L;
        Ordine ordine = spy(new Ordine());

        Prodotto p = new Prodotto();
        p.setCodProdotto("P1");
        OrdineProdotto op = new OrdineProdotto();
        op.setProdotto(p);
        op.setQuantita(5);
        ordine.addProdotto(op);

        when(ordineRepository.findById(id)).thenReturn(Optional.of(ordine));
        when(ordineRepository.save(any())).thenReturn(ordine);
        when(prodottoRepository.updateStock("P1", 5)).thenReturn(1);

        ordineService.deleteOrdine(id);

        verify(prodottoRepository).updateStock("P1", 5);
    }

    @Test
    public void deleteOrdineLanciaExceptionSeStatoErrato() {
        Ordine ordine = new Ordine();
        ordine.avanzaStato();
        ordine.avanzaStato();
        when(ordineRepository.findById(ordine.getId())).thenReturn(Optional.of(ordine));

        assertThrows(OrdineNonCancellabileException.class, () -> ordineService.deleteOrdine(ordine.getId()));
    }

    @Test
    void cambiaStatoSuccesso() {
        Long id = 1L;
        Ordine ordine = new Ordine();

        when(ordineRepository.findById(id)).thenReturn(Optional.of(ordine));
        when(ordineRepository.save(any(Ordine.class))).thenReturn(ordine);

        OrdineResponseDTO responseDTO = new OrdineResponseDTO(id, "CF123", null, "SPEDITO");
        when(ordineMapper.toResponseDTO(ordine)).thenReturn(responseDTO);

        OrdineResponseDTO result = ordineService.cambiaStato(id);

        assertNotNull(result);
        assertEquals("SPEDITO", result.stato());
        verify(ordineRepository, times(1)).save(ordine);
    }

    @Test
    public void cambiaStatoLanciaExceptionSeStatoFinale() {
        Long id = 1L;
        Ordine ordine = new Ordine();
        ordine.avanzaStato();
        ordine.avanzaStato();

        when(ordineRepository.findById(id)).thenReturn(Optional.of(ordine));

        assertThrows(OrdineNonAvanzabileException.class, () -> {
            ordineService.cambiaStato(id);
        });

        verify(ordineRepository, never()).save(any());
    }

    @Test
    public void getOrdine_Successo() {
        Long id = 1L;
        Ordine ordine = new Ordine();
        OrdineResponseDTO expected = new OrdineResponseDTO(id, "CF123", null, "NUOVO");

        when(ordineRepository.findById(id)).thenReturn(Optional.of(ordine));
        when(ordineMapper.toResponseDTO(ordine)).thenReturn(expected);

        OrdineResponseDTO result = ordineService.getOrdine(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        verify(ordineRepository).findById(id);
    }

    @Test
    public void getOrdineLanciaExceptionSeNonTrovato() {
        Long id = 99L;
        when(ordineRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(OrdineNonTrovatoException.class, () -> ordineService.getOrdine(id));
    }

    @Test
    public void getListaOrdiniSenzaPaginazioneSuccesso() {
        Ordine o1 = new Ordine();
        Ordine o2 = new Ordine();
        List<Ordine> lista = List.of(o1, o2);

        when(ordineRepository.findAll()).thenReturn(lista);
        when(ordineMapper.toResponseDTO(any())).thenReturn(new OrdineResponseDTO(1L, "CF", null, "STATO"));

       List<OrdineResponseDTO> result = ordineService.getListaOrdini();

        assertEquals(2, result.size());
        verify(ordineRepository).findAll();
    }

    @Test
    public void getListaOrdiniPaginataSuccesso() {
        Pageable pageable = PageRequest.of(0, 5);
        Ordine ordine = new Ordine();
        Page<Ordine> page = new PageImpl<>(List.of(ordine));

        when(ordineRepository.findAll(pageable)).thenReturn(page);
        when(ordineMapper.toResponseDTO(any())).thenReturn(new OrdineResponseDTO(1L, "CF", null, "STATO"));

        Page<OrdineResponseDTO> result = ordineService.getListaOrdini(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(ordineRepository).findAll(pageable);
    }

    @Test
    public void aggiungiOrdineLanciaExceptionSeAlcuniProdottiMancano() {
        String cf = "CF123";
        List<OrdineProdottoRequestDTO> prodottiRichiesti = List.of(
                new OrdineProdottoRequestDTO("P1", 1),
                new OrdineProdottoRequestDTO("P2", 1)
        );
        OrdineRequestDTO request = new OrdineRequestDTO(cf, prodottiRichiesti);

        when(clienteRepository.findById(cf)).thenReturn(Optional.of(new Cliente()));

        Prodotto p1 = new Prodotto();
        p1.setCodProdotto("P1");

        when(prodottoRepository.findAllById(any())).thenReturn(List.of(p1));

        ProdottoNonTrovatoException ex = assertThrows(ProdottoNonTrovatoException.class, () -> {
            ordineService.aggiungiOrdine(request);
        });

        assertTrue(ex.getMessage().contains("P2"));

        verify(ordineRepository, never()).save(any());
    }
}
