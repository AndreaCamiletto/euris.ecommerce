package assignment.models.stato;

public class Ordinato implements StatoOrdine{

    @Override
    public boolean cancellabile() {
        return true;
    }

    @Override
    public StatoOrdine prossimaFase() {
        return new Consegnato();
    }

    @Override
    public String getNome() {
        return "ORDINATO";
    }
}
