package assignment.mapper;

import assignment.dto.request.ClienteRequestDTO;
import assignment.dto.response.ClienteResponseDTO;
import assignment.models.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequestDTO request) {
        if(request == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        cliente.setCodFiscale(request.codFiscale());
        cliente.setCognome(request.cognome());
        cliente.setDataNascita(request.dataNascita());
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        return cliente;
    }

    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        if(cliente == null) {
            return null;
        }
        return new ClienteResponseDTO(
                cliente.getCodFiscale(),
                cliente.getNome(),
                cliente.getCognome(),
                cliente.getEmail(),
                cliente.getDataNascita());
    }
}
