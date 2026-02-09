package assignment.models.stato;

public class Ordinato implements StatoOrdine{

    @Override
    public boolean cancellabile() {
        return true;
    }

    @Override
    public StatoOrdine prossimaFase() {
        return new Spedito();
    }

    public StatoOrdine cancellazione() {return new Cancellato();}

    @Override
    public String getNome() {
        return "ORDINATO";
    }

    @Override
    public boolean avanzabile() {
        return true;
    }
}
