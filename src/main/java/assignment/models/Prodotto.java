package assignment.models;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class Prodotto {

    @NonNull
    private String codProdotto;

    private String nome;
    private Integer stock;

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
}
