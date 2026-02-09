package assignment.models;

import assignment.models.stato.Ordinato;
import assignment.models.stato.StatoOrdine;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name="ORDINE")
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdineProdotto> prodottiOrdine;

    @ManyToOne
    @JoinColumn(name="CODFISCALE", nullable=false)
    private Cliente cliente;

    @Column(name="STATO")
    private String stato;

    @Transient
    private StatoOrdine statoOrdine;

    @Version
    private Long version;

    public Ordine() {
    }

    public Ordine(List<OrdineProdotto> ordine, Cliente cliente) {
        this.prodottiOrdine = ordine;
        this.cliente = cliente;
        statoOrdine = new Ordinato();
        this.stato = getStato();
    }

    public void avanzaStato() {
        this.statoOrdine = this.statoOrdine.prossimaFase();
        this.stato = getStato();
    }

    public void avanzaStatoCancellato() {
        this.statoOrdine = this.statoOrdine.cancellazione();
        this.stato = getStato();
    }

    public boolean cancellabile() {
        return this.statoOrdine.cancellabile();
    }

    public String getStato() {
        return this.statoOrdine.getNome();
    }

    public Long getId() {
        return id;
    }

    public List<OrdineProdotto> getProdottiOrdine() {
        return prodottiOrdine;
    }

    public void setProdottiOrdine(List<OrdineProdotto> ordine) {
        this.prodottiOrdine = ordine;
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
        return Objects.equals(getProdottiOrdine(), ordine1.getProdottiOrdine()) && Objects.equals(getCliente(), ordine1.getCliente());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProdottiOrdine(), getCliente());
    }


}
