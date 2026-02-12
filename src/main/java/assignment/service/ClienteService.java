package assignment.service;

import assignment.dto.request.ClienteRequestDTO;
import assignment.dto.response.ClienteResponseDTO;
import assignment.exceptions.ClienteDuplicatoException;
import assignment.exceptions.ClienteNonTrovatoException;
import assignment.mapper.ClienteMapper;
import assignment.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final Logger log = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public List<ClienteResponseDTO> getListaClienti() {//senza paginazione per task 2
        log.debug("Recupero lista completa clienti da database");
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toResponseDTO)
                .toList();
    }

    public Page<ClienteResponseDTO> getListaClienti(Pageable pageable) {
        log.debug("Recupero lista completa clienti da database {}", pageable);
        return clienteRepository.findAll(pageable)
                .map(clienteMapper::toResponseDTO);
    }

    public ClienteResponseDTO aggiungiCliente(ClienteRequestDTO clienteRequest){
        log.info("Tentativo di salvataggio nuovo cliente: {}", clienteRequest.codFiscale());
        if(clienteRepository.existsById(clienteRequest.codFiscale())) {
            log.warn("Salvataggio fallito: Cliente {} già presente a sistema", clienteRequest.codFiscale());
            throw new ClienteDuplicatoException("Cliente già presente: " + clienteRequest.codFiscale());
        }
        return clienteMapper.toResponseDTO(clienteRepository.save(clienteMapper.toEntity(clienteRequest)));
    }

    public ClienteResponseDTO getCliente(String codFiscale) {
        log.debug("Ricerca cliente per CF: {}", codFiscale);
        return clienteRepository.
                findById(codFiscale)
                .map(clienteMapper::toResponseDTO)
                .orElseThrow(() -> {
                    log.error("Ricerca fallita: Cliente con CF {} non esiste", codFiscale);
                    return new ClienteNonTrovatoException("Cliente " + codFiscale + " non trovato");
                });
    }
}
