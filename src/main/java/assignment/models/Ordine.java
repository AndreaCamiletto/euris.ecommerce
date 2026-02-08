package assignment.models;

import assignment.models.stato.Ordinato;
import assignment.models.stato.StatoOrdine;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Ordine {
    private String id;
    private Map<Prodotto,Integer> ordine;
    private Cliente cliente;
    private StatoOrdine stato;

    public Ordine(Map<Prodotto, Integer> ordine, Cliente cliente) {
        this.id = UUID.randomUUID().toString();
        this.ordine = ordine;
        this.cliente = cliente;
        this.stato = new Ordinato();
    }

    public void avanzaStato() {
        this.stato = this.stato.prossimaFase();
    }

    public boolean cancellabile() {
        return stato.cancellabile();
    }

    public String getStato() {
        return stato.getNome();
    }

    public String getId() {
        return id;
    }

    public Map<Prodotto, Integer> getOrdine() {
        return ordine;
    }

    public void setOrdine(Map<Prodotto, Integer> ordine) {
        this.ordine = ordine;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ordine ordine1)) return false;
        return Objects.equals(getOrdine(), ordine1.getOrdine()) && Objects.equals(getCliente(), ordine1.getCliente());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrdine(), getCliente());
    }


}
