package assignment.models.stato;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CancellatoTest {

    private final Cancellato stato = new Cancellato();

    @Test
    public void testMetodiBase() {
        assertEquals("CANCELLATO", stato.getNome());

        assertFalse(stato.avanzabile());
        assertFalse(stato.cancellabile());
    }

    @Test
    public void testProssimaFase() {
        assertEquals(stato, stato.prossimaFase());
    }

    @Test
    public void testCancellazioneErrata() {
        assertThrows(UnsupportedOperationException.class, () -> {
            stato.cancellazione();
        });
    }
}