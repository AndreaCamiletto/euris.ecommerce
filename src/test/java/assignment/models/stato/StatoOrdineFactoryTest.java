package assignment.models.stato;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StatoOrdineFactoryTest {

    @Test
    public void testFromNome_CasiValidi() {
        assertTrue(StatoOrdineFactory.fromNome("ORDINATO") instanceof Ordinato);
        assertTrue(StatoOrdineFactory.fromNome("SPEDITO") instanceof Spedito);
        assertTrue(StatoOrdineFactory.fromNome("CONSEGNATO") instanceof Consegnato);
        assertTrue(StatoOrdineFactory.fromNome("CANCELLATO") instanceof Cancellato);
    }

    @Test
    public void testFromNome_Null() {
        StatoOrdine stato = StatoOrdineFactory.fromNome(null);
        assertTrue(stato instanceof Ordinato);
    }

    @Test
    public void testFromNome_Invalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            StatoOrdineFactory.fromNome("STATO_INVENTATO");
        });

        assertTrue(exception.getMessage().contains("Stato non valido"));
    }
}