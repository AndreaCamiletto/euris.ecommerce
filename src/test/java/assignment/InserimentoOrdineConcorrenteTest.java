package assignment;

import assignment.dto.request.OrdineProdottoRequestDTO;
import assignment.dto.request.OrdineRequestDTO;
import assignment.exceptions.ProdottoOutOfStockException;
import assignment.models.Cliente;
import assignment.models.Prodotto;
import assignment.repository.ClienteRepository;
import assignment.repository.ProdottoRepository;
import assignment.service.OrdineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InserimentoOrdineConcorrenteTest {

    @Autowired
    private OrdineService ordineService;

    @Autowired
    private ProdottoRepository prodottoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void stockLimitato() throws Exception {

        String codProd = "PROD_CONC";
        Prodotto prodotto = new Prodotto();
        prodotto.setCodProdotto(codProd);
        prodotto.setNome("Prodotto Limitato");
        prodotto.setStock(2);
        prodottoRepository.save(prodotto);
        prodottoRepository.flush();

        String codProd2 = "PROD_CONC2";
        Prodotto prodotto2 = new Prodotto();
        prodotto2.setCodProdotto(codProd2);
        prodotto2.setNome("Prodotto Limitato");
        prodotto2.setStock(1);
        prodottoRepository.saveAndFlush(prodotto2);

        // --- Setup clienti ---
        Cliente clienteA = new Cliente(
                "Mario",
                "Rossi",
                "USER_A",
                LocalDate.of(1990, 1, 1),
                "a@test.com"
        );

        Cliente clienteB = new Cliente(
                "Luca",
                "Bianchi",
                "USER_B",
                LocalDate.of(1992, 2, 2),
                "b@test.com"
        );

        clienteRepository.saveAndFlush(clienteA);
        clienteRepository.saveAndFlush(clienteB);

        OrdineRequestDTO reqA = new OrdineRequestDTO(
                "USER_A",
                List.of(new OrdineProdottoRequestDTO(codProd, 1))
        );

        OrdineRequestDTO reqB = new OrdineRequestDTO(
                "USER_B",
                List.of(new OrdineProdottoRequestDTO(codProd, 1))
        );

        OrdineRequestDTO reqC = new OrdineRequestDTO(
                "USER_B",
                List.of(new OrdineProdottoRequestDTO(codProd2, 1), new OrdineProdottoRequestDTO(codProd2, 1))
        );

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<String> taskA = () -> {
            startLatch.await();
            try {
                ordineService.aggiungiOrdine(reqA);
                return "SUCCESS";
            } catch (Exception e) {
                return e.getClass().getSimpleName();
            }
        };

        Callable<String> taskB = () -> {
            startLatch.await();
            try {
                ordineService.aggiungiOrdine(reqB);
                return "SUCCESS";
            } catch (Exception e) {
                return e.getClass().getSimpleName();
            }
        };

        Callable<String> taskC = () -> {
            startLatch.await();
            try {
                ordineService.aggiungiOrdine(reqB);
                return "SUCCESS";
            } catch (Exception e) {
                return e.getClass().getSimpleName();
            }
        };

        Future<String> futureA = executor.submit(taskA);
        Future<String> futureB = executor.submit(taskB);
        Future<String> futureC = executor.submit(taskC);

        startLatch.countDown();

        String resA = futureA.get();
        String resB = futureB.get();
        String resC = futureC.get();

        executor.shutdown();

        long successCount = List.of(resA, resB, resC).stream()
                .filter(r -> r.equals("SUCCESS"))
                .count();

        long failureCount = List.of(resA, resB, resC).stream()
                .filter(r -> r.equals(ProdottoOutOfStockException.class.getSimpleName()))
                .count();

        assertEquals(2, successCount);
        assertEquals(1, failureCount);

        Prodotto finale = prodottoRepository.findById(codProd).orElseThrow();
        assertEquals(0, finale.getStock());
    }

    @Test
    void concorrenzaMassivaTest() throws Exception {
        String codProd = "PROD_STRESS";
        Prodotto prodotto = new Prodotto();
        prodotto.setCodProdotto(codProd);
        prodotto.setNome("Prodotto Stress Test");
        prodotto.setStock(10);
        prodottoRepository.saveAndFlush(prodotto);

        Cliente cliente = new Cliente("Test", "User", "CF_STRESS", LocalDate.now(), "stress@test.com");
        clienteRepository.saveAndFlush(cliente);

        int numeroThread = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numeroThread);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < numeroThread; i++) {
            Callable<String> task = () -> {
                startLatch.await(); // partenza simultanea
                try {
                    ordineService.aggiungiOrdine(
                            new OrdineRequestDTO("CF_STRESS",
                                    List.of(new OrdineProdottoRequestDTO(codProd, 1)))
                    );
                    return "SUCCESS";
                } catch (Exception e) {
                    return e.getClass().getSimpleName();
                }
            };
            futures.add(executor.submit(task));
        }

        startLatch.countDown(); // via ai thread

        List<String> risultati = new ArrayList<>();
        for (Future<String> f : futures) {
            try {
                risultati.add(f.get(5, TimeUnit.SECONDS));
            } catch (TimeoutException te) {
                risultati.add("TIMEOUT");
            }
        }

        executor.shutdown();

        long ordiniRiusciti = risultati.stream().filter(r -> r.equals("SUCCESS")).count();
        long ordiniFalliti = risultati.stream().filter(r -> r.equals(ProdottoOutOfStockException.class.getSimpleName())).count();
        long timeout = risultati.stream().filter(r -> r.equals("TIMEOUT")).count();

        System.out.println("Ordini riusciti: " + ordiniRiusciti);
        System.out.println("Ordini falliti (Out of Stock): " + ordiniFalliti);
        System.out.println("Timeout: " + timeout);

        assertEquals(10, ordiniRiusciti, "Solo 10 ordini dovevano avere successo");
        assertEquals(numeroThread - 10, ordiniFalliti, "10 ordini dovevano fallire");
        assertEquals(0, timeout, "Non devono esserci timeout");

        Prodotto finale = prodottoRepository.findById(codProd).orElseThrow();
        assertEquals(0, finale.getStock(), "Lo stock finale deve essere 0");
    }


}
