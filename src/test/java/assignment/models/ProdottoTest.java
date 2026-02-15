package assignment.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProdottoTest {

    @Test
    public void testProdottoCompleto() {
        Prodotto p = new Prodotto();
        p.setCodProdotto("P1");
        p.setNome("Pasta");
        p.setStock(100);

        assertEquals("P1", p.getCodProdotto());
        assertEquals("Pasta", p.getNome());
        assertEquals(100, p.getStock());
        assertNotNull(p.toString());
    }

    @Test
    public void testEqualsEHashCode() {
        Prodotto p1 = new Prodotto();
        p1.setCodProdotto("A");

        Prodotto p2 = new Prodotto();
        p2.setCodProdotto("A");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}