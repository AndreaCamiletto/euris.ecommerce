package assignment.controller;

import assignment.dto.request.ProdottoRequestDTO;
import assignment.dto.response.ProdottoResponseDTO;
import assignment.exceptions.ProdottoDuplicatoException;
import assignment.exceptions.ProdottoNonTrovatoException;
import assignment.service.ProdottoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdottoController.class)
class ProdottoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProdottoService prodottoService;

    @Test
    public void getListaProdottiSenzaPaginazioneRitornaOk() throws Exception {
        when(prodottoService.getListaProdotti()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/prodotti"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getListaProdottiPaginataRitornaOk() throws Exception {
        when(prodottoService.getListaProdotti(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/prodotti")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    public void aggiungiProdottoSuccessoRitornaCreated() throws Exception {
        ProdottoRequestDTO request = new ProdottoRequestDTO("P001", "Laptop", 10);
        ProdottoResponseDTO response = new ProdottoResponseDTO("P001", "Laptop", 10);

        when(prodottoService.aggiungiProdotto(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/prodotti")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codProdotto").value("P001"))
                .andExpect(jsonPath("$.nome").value("Laptop"));
    }

    @Test
    public void aggiungiProdottoErroreDuplicatoRitornaConflict() throws Exception {
        ProdottoRequestDTO request = new ProdottoRequestDTO("P001", "Laptop", 10);

        when(prodottoService.aggiungiProdotto(any()))
                .thenThrow(new ProdottoDuplicatoException("Codice già esistente"));

        mockMvc.perform(post("/api/v1/prodotti")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void getProdottoNonTrovatoRitornaNotFound() throws Exception {
        when(prodottoService.getProdotto("FAKE"))
                .thenThrow(new ProdottoNonTrovatoException("Prodotto non trovato"));

        mockMvc.perform(get("/api/v1/prodotti/FAKE"))
                .andExpect(status().isNotFound());
    }
}
