package assignment.models.stato;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpeditoTest {

    private final Spedito stato = new Spedito();

    @Test
    public void testMetodiBase() {
        assertEquals("SPEDITO", stato.getNome());
        assertTrue(stato.avanzabile());
        assertTrue(stato.cancellabile());
    }

    @Test
    public void testProssimaFase() {
        StatoOrdine prossima = stato.prossimaFase();
        assertTrue(prossima instanceof Consegnato);
        assertEquals("CONSEGNATO", prossima.getNome());
    }

    @Test
    public void testCancellazione() {
        StatoOrdine cancellato = stato.cancellazione();
        assertTrue(cancellato instanceof Cancellato);
        assertEquals("CANCELLATO", cancellato.getNome());
    }
}