Il progetto è stato sviluppato con Java 17 e Spring Boot 3.4.3. 
L'applicazione gestisce il ciclo di vita degli ordini, l'anagrafica clienti e l'inventario prodotti, 
con una particolare attenzione sulla transazionalità e la gestione della concorrenza.

Il progetto segue un'architettura a layer (Controller -> Service -> Repository), segue i principi SOLID e implementa diversi Design Pattern per permetterne manutenibilità e scalabilità:

- State Pattern: Gestione del ciclo di vita dell'ordine. Gli stati (Ordinato, Spedito, Consegnato, Cancellato) sono incapsulati in classi dedicate che implementano l'interfaccia StatoOrdine;

- Factory Pattern: Utilizzato in StatoOrdineFactory per istanziare dinamicamente lo stato corretto a partire dai dati di persistenza;

- Data Transfer Object (DTO): Disaccoppiamento tra le entità JPA e l'interfaccia REST per proteggere l'integrità del database;

- Global Exception Handling: Gestione centralizzata degli errori tramite @RestControllerAdvice, che restituisce messaggi chiari e codici HTTP appropriati.

Per la gestione di ordini contemporanei da parte di uno o più utenti, sono state utilizzate le seguenti misure:

- Query Atomiche: Aggiornamento dello stock tramite query SQL che verificano la disponibilità direttamente a livello di database;

- Isolation: Gestione delle transazioni con @Transactional.

I dati sono salvati su un database h2 in memory.
All'avvio vengono precaricati nel database alcuni dati di prova.

Il progetto è stato testato attraverso test end-to-end su swagger e tramite Unit Test. Un ulteriore test, InserimentoOrdineConcorrenteTest, simula 20 thread simultanei su uno stock limitato per verificarne la robustezza.

L'applicazione si può testare da http://localhost:8080/swagger-ui/index.html oppure con il comando curl.
Di seguito alcune curl:
- curl -X GET "http://localhost:8080/api/v1/prodotti"
- curl -X POST "http://localhost:8080/api/v1/prodotti" -H "Content-Type: application/json" -d "{\"codProdotto\": \"PROD_TEST\", \"nome\": \"Prodotto Test\", \"prezzo\": 19.99, \"stock\": 50}"
- curl -X GET "http://localhost:8080/api/v1/clienti/CF001"
- curl -X POST "http://localhost:8080/api/v1/ordini" -H "Content-Type: application/json" -d "{\"codFiscale\": \"CF001\", \"prodottiOrdine\": [{\"codProdotto\": \"P001\", \"quantita\": 2}]}"
- curl -X PATCH "http://localhost:8080/api/v1/ordini/1/stato"
- curl -X DELETE "http://localhost:8080/api/v1/ordini/1"
