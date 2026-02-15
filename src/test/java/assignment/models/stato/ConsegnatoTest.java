package assignment.models.stato;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConsegnatoTest {

    private final Consegnato stato = new Consegnato();

    @Test
    public void testMetodiStatoFinale() {
        assertEquals("CONSEGNATO", stato.getNome());

        assertFalse(stato.avanzabile());
        assertFalse(stato.cancellabile());
    }

    @Test
    public void testProssimaFase() {
        assertEquals(stato, stato.prossimaFase());
    }

    @Test
    public void testCancellazioneEsplosiva() {
        assertThrows(UnsupportedOperationException.class, () -> {
            stato.cancellazione();
        });
    }
}