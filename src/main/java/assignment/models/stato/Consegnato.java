package assignment.models.stato;

public class Consegnato implements StatoOrdine{

    @Override
    public boolean cancellabile() {
        return false;
    }

    @Override
    public StatoOrdine prossimaFase() {
        return this;
    }

    @Override
    public String getNome() {
        return "CONSEGNATO";
    }

}
