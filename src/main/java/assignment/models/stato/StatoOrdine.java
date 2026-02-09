package assignment.models.stato;

public interface StatoOrdine {
    boolean cancellabile();
    StatoOrdine prossimaFase();
    StatoOrdine cancellazione();
    String getNome();

}
