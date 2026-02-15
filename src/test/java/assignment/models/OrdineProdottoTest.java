package assignment.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrdineProdottoTest {

    @Test
    public void testCostruttoreCompletoEGetSet() {
        Ordine ordine = new Ordine();
        Prodotto prodotto = new Prodotto();
        Integer quantita = 10;

        OrdineProdotto op = new OrdineProdotto(ordine, prodotto, quantita);

        op.setOrdine(ordine);
        op.setProdotto(prodotto);

        assertEquals(ordine, op.getOrdine());
        assertEquals(prodotto, op.getProdotto());
        assertEquals(ordine, op.getOrdine());
        assertEquals(prodotto, op.getProdotto());
        assertEquals(quantita, op.getQuantita());

        op.setId(1L);
        assertEquals(1L, op.getId());

        op.setQuantita(20);
        assertEquals(20, op.getQuantita());
    }

    @Test
    public void testCostruttoreVuoto() {
        OrdineProdotto op = new OrdineProdotto();
        assertNotNull(op);
    }

    @Test
    public void testEqualsEHashCode() {
        OrdineProdotto op1 = new OrdineProdotto();
        op1.setId(100L);

        OrdineProdotto op2 = new OrdineProdotto();
        op2.setId(100L);

        OrdineProdotto op3 = new OrdineProdotto();
        op3.setId(200L);

        assertEquals(op1, op2);
        assertNotEquals(op1, op3);
        assertEquals(op1.hashCode(), op2.hashCode());

        assertNotEquals(null, op1);
        assertNotEquals("stringa", op1);
    }
}