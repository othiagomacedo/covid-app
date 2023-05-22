package covid.application.api.repository;

import covid.application.api.modelos.entidade.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {
    @Query("SELECT p FROM Pais p WHERE p.nome = :nome")
    Optional<Pais> findByNome(String nome);

    @Query("SELECT p FROM Pais p WHERE p.sigla = :sigla")
    Optional<Pais> findBySigla(String sigla);
}
