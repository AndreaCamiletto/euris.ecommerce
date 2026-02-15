package assignment.models;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class OrdineTest {

    @Test
    public void testCostruttoreEGetSet() {
        Ordine o = new Ordine();
        o.setCliente(new Cliente("CF123"));

        assertEquals("ORDINATO", o.getStato());
        assertEquals("CF123", o.getCliente().getCodFiscale());
        assertNotNull(o.getOrdineProdotto());
    }

    @Test
    public void testGestioneProdotti() {
        Ordine o = new Ordine();
        OrdineProdotto op = new OrdineProdotto();

        o.addProdotto(op);
        assertEquals(1, o.getOrdineProdotto().size());
        assertEquals(o, op.getOrdine());

        o.removeProdotto(op);
        assertTrue(o.getOrdineProdotto().isEmpty());
        assertNull(op.getOrdine());
    }

    @Test
    public void testSetProdottiOrdine() {
        Ordine o = new Ordine();
        List<OrdineProdotto> lista = new ArrayList<>();
        lista.add(new OrdineProdotto());
        lista.add(new OrdineProdotto());

        o.setProdottiOrdine(lista);
        assertEquals(2, o.getOrdineProdotto().size());

        o.setProdottiOrdine(null);
        assertTrue(o.getOrdineProdotto().isEmpty());
    }

    @Test
    public void testAvanzamentoStato() {
        Ordine o = new Ordine();

        assertTrue(o.avanzabile());
        assertTrue(o.cancellabile());

        o.avanzaStato();
        assertNotNull(o.getStato());

        o = new Ordine();
        o.avanzaStatoCancellato();
        assertEquals("CANCELLATO", o.getStato());
    }

    @Test
    public void testEqualsEHashCode() {
        Ordine o1 = new Ordine();
        Ordine o2 = new Ordine();
        assertEquals(o1, o2); // null uguale null
        assertEquals(o1.hashCode(), o2.hashCode());

        assertFalse(o1.equals(new Object()));
    }
}