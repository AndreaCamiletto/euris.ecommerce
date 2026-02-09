package assignment.models.stato;

public class Cancellato implements StatoOrdine{

    @Override
    public boolean cancellabile() {
        return false;
    }

    @Override
    public StatoOrdine prossimaFase() {
        return this;
    }

    @Override
    public StatoOrdine cancellazione() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNome() {
        return "CANCELLATO";
    }

    @Override
    public boolean avanzabile() {
        return false;
    }
}
