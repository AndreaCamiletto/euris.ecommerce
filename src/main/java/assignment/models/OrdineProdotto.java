package assignment.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="ORDINE_PRODOTTO")
public class OrdineProdotto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ORDINE_ID")
    private Ordine ordine;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PRODOTTO_ID")
    private Prodotto prodotto;

    @Column(name = "QUANTITA", nullable = false)
    private Integer quantita;

    public OrdineProdotto() {}

    public OrdineProdotto(Ordine ordine, Prodotto prodotto, Integer quantita) {
        this.ordine = ordine;
        this.prodotto = prodotto;
        this.quantita = quantita;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ordine getOrdine() {
        return ordine;
    }

    public void setOrdine(Ordine ordine) {
        this.ordine = ordine;
    }

    public Prodotto getProdotto() {
        return prodotto;
    }

    public void setProdotto(Prodotto prodotto) {
        this.prodotto = prodotto;
    }

    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrdineProdotto that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
