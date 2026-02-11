package assignment.service;

import assignment.dto.request.ClienteRequestDTO;
import assignment.dto.request.OrdineRequestDTO;
import assignment.dto.request.ProdottoRequestDTO;
import assignment.dto.response.ClienteResponseDTO;
import assignment.dto.response.OrdineResponseDTO;
import assignment.dto.response.ProdottoResponseDTO;
import assignment.models.Cliente;
import assignment.models.Ordine;
import assignment.models.Prodotto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EcommerceInterface {

    ClienteResponseDTO aggiungiCliente(ClienteRequestDTO cliente);
    List<ClienteResponseDTO> getClienti();
    Page<ClienteResponseDTO> getClientiPaginati(Pageable pageable);
    ProdottoResponseDTO aggiungiProdotto(ProdottoRequestDTO prodotto);
    List<ProdottoResponseDTO> getProdotti();
    Page<ProdottoResponseDTO> getProdottiPaginati(Pageable pageable);
    OrdineResponseDTO aggiungiOrdine(OrdineRequestDTO ordine);
    List<OrdineResponseDTO> getOrdini();
    Page<OrdineResponseDTO> getOrdiniPaginati(Pageable pageable);

}
