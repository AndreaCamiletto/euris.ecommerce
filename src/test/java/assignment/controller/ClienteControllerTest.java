package assignment.controller;

import assignment.dto.request.ClienteRequestDTO;
import assignment.dto.response.ClienteResponseDTO;
import assignment.exceptions.ClienteDuplicatoException;
import assignment.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService clienteService;

    @Test
    public void getListaClientiSenzaParametriRitornaListaCompleta() throws Exception {
        when(clienteService.getListaClienti()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/clienti"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(clienteService, times(1)).getListaClienti();
    }

    @Test
    public void getListaClientiConParametroPageRitornaPaginata() throws Exception {
        Page<ClienteResponseDTO> page = new PageImpl<>(List.of());
        when(clienteService.getListaClienti(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/clienti")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(clienteService, times(1)).getListaClienti(any(Pageable.class));
    }

    @Test
    public void getClienteSuccesso() throws Exception {
        String cf = "RSSMRA80A01H501U";
        ClienteResponseDTO dto = new ClienteResponseDTO(cf, "Mario", "Rossi", "m@r.it", LocalDate.now());
        when(clienteService.getCliente(cf)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/clienti/" + cf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codFiscale").value(cf))
                .andExpect(jsonPath("$.nome").value("Mario"));
    }

    @Test
    public void aggiungiClienteSuccessoRitornaCreated() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO("RSSMRA80A01H501U", "Mario", "Rossi", "m@r.it", LocalDate.now());
        ClienteResponseDTO response = new ClienteResponseDTO("RSSMRA80A01H501U", "Mario", "Rossi", "m@r.it", LocalDate.now());

        when(clienteService.aggiungiCliente(any(ClienteRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/clienti")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // Verifica lo status 201
                .andExpect(jsonPath("$.codFiscale").value("RSSMRA80A01H501U"));
    }

    @Test
    public void aggiungiClienteErroreClienteDuplicatoRitorna409() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO("DUPLICATO", "N", "C", "e@e.it", LocalDate.now());

        when(clienteService.aggiungiCliente(any())).thenThrow(new ClienteDuplicatoException("Esiste già"));

        mockMvc.perform(post("/api/v1/clienti")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()); // 409 Conflict
    }
}