package assignment.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

    @Test
    public void testCostruttore() {
        Cliente c = new Cliente();
        c.setNome("Mario");
        c.setCognome("Rossi");
        c.setCodFiscale("RSSMRA80A01H501U");
        c.setDataNascita(LocalDate.of(1980, 1, 1));
        c.setEmail("mario@email.com");

        assertEquals("Mario", c.getNome());
        assertEquals("Rossi", c.getCognome());
        assertEquals("RSSMRA80A01H501U", c.getCodFiscale());
        assertEquals(LocalDate.of(1980, 1, 1), c.getDataNascita());
        assertEquals("mario@email.com", c.getEmail());
    }

    @Test
    public void testCostruttoriArgomenti() {
        Cliente c1 = new Cliente("RSSMRA80A01H501U");
        assertEquals("RSSMRA80A01H501U", c1.getCodFiscale());

        Cliente c2 = new Cliente("Mario", "Rossi", "RSSMRA80A01H501U", LocalDate.now(), "m@m.it");
        assertNotNull(c2.getNome());
    }

    @Test
    public void testErroriCodFiscale() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente(null));
        assertThrows(IllegalArgumentException.class, () -> new Cliente(""));

        Cliente c = new Cliente("OK123");
        assertThrows(IllegalArgumentException.class, () -> c.setCodFiscale(" "));
    }

    @Test
    public void testMetodiStandard() {
        Cliente c1 = new Cliente("CF1");
        Cliente c2 = new Cliente("CF1");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());

        assertNotNull(c1.toString());
    }
}