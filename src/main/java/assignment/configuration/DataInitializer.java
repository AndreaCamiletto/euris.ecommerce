package assignment.configuration;

import assignment.dto.request.ClienteRequestDTO;
import assignment.dto.request.OrdineProdottoRequestDTO;
import assignment.dto.request.OrdineRequestDTO;
import assignment.dto.request.ProdottoRequestDTO;
import assignment.service.ClienteService;
import assignment.service.OrdineService;
import assignment.service.ProdottoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(ClienteService clienteService,
                                   ProdottoService prodottoService,
                                   OrdineService ordineService) {
        return args -> {
            log.info("Inizio precaricamento database...");

            try {
                clienteService.aggiungiCliente(new ClienteRequestDTO("CF001", "Mario", "Rossi", "aaaa@bbbb.it", LocalDate.of(1985, 5, 20)));
                clienteService.aggiungiCliente(new ClienteRequestDTO("CF002",  "Luca","Bianchi", "aaaa@bbbb.it", LocalDate.of(1992, 11, 10)));
                log.info("Clienti inseriti.");

                prodottoService.aggiungiProdotto(new ProdottoRequestDTO("P001", "Laptop", 10));
                prodottoService.aggiungiProdotto(new ProdottoRequestDTO("P002", "Mouse", 50));
                prodottoService.aggiungiProdotto(new ProdottoRequestDTO("P003", "Tastiera Meccanica", 20));
                log.info("Prodotti inseriti.");
                List<OrdineProdottoRequestDTO> prodottiOrdine = List.of(
                        new OrdineProdottoRequestDTO("P001", 1),
                        new OrdineProdottoRequestDTO("P002", 2)
                );

                ordineService.aggiungiOrdine(new OrdineRequestDTO("CF001", prodottiOrdine));
                log.info("Ordine di esempio creato con successo.");

                log.info(">>> DATABASE PRONTO: Precaricamento completato con successo.");

            } catch (Exception e) {
                log.warn("Nota: Errore durante l'inizializzazione dei dati (potrebbero essere già presenti). Errore: {}", e.getMessage());
            }
        };
    }
}