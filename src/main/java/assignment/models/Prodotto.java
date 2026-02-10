package assignment.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="PRODOTTO")
public class Prodotto {

    @Id
    @Column(name="CODICE_PRODOTTO", nullable = false, unique = true)
    private String codProdotto;

    @Column(name="NOME")
    private String nome;

    @Column(name="STOCK", nullable = false)
    private Integer stock;

    @Version
    private Long version;

    public Prodotto() {}

    public String getCodProdotto() {
        return codProdotto;
    }

    public void setCodProdotto(String codProdotto) {
        this.codProdotto = codProdotto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        if(stock < 0) {
            throw new IllegalArgumentException("Lo stock deve essere maggiore di 0");
        }
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Prodotto{" +
                "codProdotto='" + codProdotto + '\'' +
                ", nome='" + nome + '\'' +
                ", stock=" + stock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Prodotto prodotto)) return false;
        return Objects.equals(getCodProdotto(), prodotto.getCodProdotto());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCodProdotto());
    }

    public Long getVersion() {
        return version;
    }
}
