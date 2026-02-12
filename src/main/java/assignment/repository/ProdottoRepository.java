package assignment.repository;

import assignment.models.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto, String> {

    @Modifying
    @Query("UPDATE Prodotto p SET p.stock = p.stock + :qty WHERE p.codProdotto = :cod AND (p.stock + :qty) >= 0")
    int updateStock(@Param("cod") String cod, @Param("qty") int qty);
}
