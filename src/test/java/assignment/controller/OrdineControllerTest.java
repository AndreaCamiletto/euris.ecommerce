package assignment.controller;

import assignment.dto.request.OrdineProdottoRequestDTO;
import assignment.dto.request.OrdineRequestDTO;
import assignment.dto.response.OrdineResponseDTO;
import assignment.service.OrdineService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdineController.class)
class OrdineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrdineService ordineService;

    @Test
    public void getListaOrdiniCompletaRitornaOk() throws Exception {
        when(ordineService.getListaOrdini()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/ordini"))
                .andExpect(status().isOk());
    }

    @Test
    public void getListaOrdiniPaginataRitornaOk() throws Exception {
        when(ordineService.getListaOrdini(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/ordini")
                        .param("page", "0"))
                .andExpect(status().isOk());
    }

    @Test
    public void aggiungiOrdineSuccessoRitornaCreated() throws Exception {
        OrdineProdottoRequestDTO rigaProdotto = new OrdineProdottoRequestDTO("P001", 2);
        OrdineRequestDTO request = new OrdineRequestDTO("RSSMRA80A01H501U", List.of(rigaProdotto));
        OrdineResponseDTO response = new OrdineResponseDTO(1L, "CF123", List.of(), "ORDINATO");

        when(ordineService.aggiungiOrdine(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/ordini")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.stato").value("ORDINATO"));
    }

    @Test
    public void getOrdineSuccesso() throws Exception {
        OrdineResponseDTO response = new OrdineResponseDTO(1L, "CF123", List.of(), "NUOVO");
        when(ordineService.getOrdine(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/ordini/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void deleteOrdineSuccesso() throws Exception {
        OrdineResponseDTO response = new OrdineResponseDTO(1L, "CF123", List.of(), "CANCELLATO");
        when(ordineService.deleteOrdine(1L)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/ordini/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stato").value("CANCELLATO"));
    }

    @Test
    public void avanzaStatoSuccesso() throws Exception {
        OrdineResponseDTO response = new OrdineResponseDTO(1L, "CF123", List.of(), "SPEDITO");
        when(ordineService.cambiaStato(1L)).thenReturn(response);

        // Usiamo patchMapping come definito nel controller
        mockMvc.perform(patch("/api/v1/ordini/1/stato"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stato").value("SPEDITO"));
    }
}
