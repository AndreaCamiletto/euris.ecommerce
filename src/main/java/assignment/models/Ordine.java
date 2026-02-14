package assignment.models;

import assignment.models.stato.Ordinato;
import assignment.models.stato.StatoOrdine;
import assignment.models.stato.StatoOrdineFactory;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="ORDINE")
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @JsonProperty("prodottiOrdine")
    private List<OrdineProdotto> ordineProdotto = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="CODFISCALE", nullable=false)
    private Cliente cliente;

    @Column(name="STATO")
    private String stato;

    @Transient
    private StatoOrdine statoOrdine;

    @PostLoad
    private void initStatoOrdine() {
        this.statoOrdine = StatoOrdineFactory.fromNome(this.stato);
    }

    public Ordine() {
        this.ordineProdotto = new ArrayList<>();
        this.statoOrdine = new Ordinato();
        this.stato = "ORDINATO";
    }

    public void addProdotto(OrdineProdotto prodottoOrdine) {
        ordineProdotto.add(prodottoOrdine);
        prodottoOrdine.setOrdine(this);
    }



    public void removeProdotto(OrdineProdotto prodottoOrdine) {
        ordineProdotto.remove(prodottoOrdine);
        prodottoOrdine.setOrdine(null);
    }

    public void avanzaStato() {
        this.statoOrdine = this.statoOrdine.prossimaFase();
        this.stato = getStato();
    }

    public void avanzaStatoCancellato() {
        this.statoOrdine = this.statoOrdine.cancellazione();
        this.stato = getStato();
    }

    public boolean avanzabile() {
        return this.statoOrdine.avanzabile();
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

    public List<OrdineProdotto> getOrdineProdotto() {
        return ordineProdotto;
    }

    public void setProdottiOrdine(List<OrdineProdotto> prodottiOrdine) {
        this.ordineProdotto.clear();
        if (prodottiOrdine != null) {
            for (OrdineProdotto op : prodottiOrdine) {
                addProdotto(op);
            }
        }
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ordine ordine)) return false;
        return Objects.equals(getId(), ordine.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
