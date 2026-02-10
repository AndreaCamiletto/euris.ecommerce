package assignment.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="CLIENTE")
public class Cliente {

    @Id
    @Column(name="CODICE_FISCALE", nullable = false, unique = true)
    private String codFiscale;

    @Column(name="NOME")
    private String nome;

    @Column(name="COGNOME")
    private String cognome;

    @Column(name="DATA_NASCITA")
    private LocalDate dataNascita;

    @Column(name="EMAIL")
    private String email;

    public Cliente() {
    }

    public Cliente(String codFiscale) {
        if (codFiscale == null || codFiscale.isBlank()) {
            throw new IllegalArgumentException("Il codice fiscale non può essere nullo o vuoto");
        }
        this.codFiscale = codFiscale;
    }

    public Cliente(String nome, String cognome, String codFiscale, LocalDate dataNascita, String email) {
        if (codFiscale == null || codFiscale.isBlank()) {
            throw new IllegalArgumentException("Il codice fiscale non può essere nullo o vuoto");
        }
        this.nome = nome;
        this.cognome = cognome;
        this.codFiscale = codFiscale;
        this.dataNascita = dataNascita;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodFiscale() {
        return codFiscale;
    }

    public void setCodFiscale(String codFiscale) {
        if (codFiscale == null || codFiscale.isBlank()) {
            throw new IllegalArgumentException("Il codice fiscale non può essere nullo o vuoto");
        }
        this.codFiscale = codFiscale;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", codFiscale='" + codFiscale + '\'' +
                ", dataNascita=" + dataNascita +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cliente cliente)) return false;
        return Objects.equals(getCodFiscale(), cliente.getCodFiscale());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCodFiscale());
    }
}
