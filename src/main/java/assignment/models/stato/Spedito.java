package assignment.models.stato;

public class Spedito implements StatoOrdine {

    @Override
    public boolean cancellabile() {
        return true;
    }

    @Override
    public StatoOrdine prossimaFase() {
        return new Consegnato();
    }

    @Override
    public StatoOrdine cancellazione() {
        return new Cancellato();
    }

    @Override
    public String getNome() {
        return "SPEDITO";
    }

    @Override
    public boolean avanzabile() {
        return true;
    }

}
