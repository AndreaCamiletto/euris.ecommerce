package assignment.models.stato;

public class StatoOrdineFactory {
    public static StatoOrdine fromNome(String nome) {
        return switch (nome) {
            case "Ordinato" -> new Ordinato();
            case "Spedito" -> new Spedito();
            case "Consegnato" -> new Consegnato();
            case "Cancellato" -> new Cancellato();
            default -> throw new IllegalArgumentException("Stato non valido: " + nome);
        };
    }
}
