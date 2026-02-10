package assignment.models.stato;

public class StatoOrdineFactory {
    public static StatoOrdine fromNome(String nome) {
        if (nome == null) {
            return new Ordinato(); // o uno stato di default
        }
        return switch (nome) {
            case "ORDINATO" -> new Ordinato();
            case "SPEDITO" -> new Spedito();
            case "CONSEGNATO" -> new Consegnato();
            case "CANCELLATO" -> new Cancellato();
            default -> throw new IllegalArgumentException("Stato non valido: " + nome);
        };
    }
}
