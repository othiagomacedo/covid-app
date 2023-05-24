package covid.application.api.repository;

import covid.application.api.modelos.entidade.HistoricoPais;
import covid.application.api.modelos.entidade.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoricoPaisRepository extends JpaRepository<HistoricoPais, Long> {

    @Query("SELECT h FROM historico_pais h WHERE 1=1 AND h.paisId = :pais AND h.dataInicial = :dataInicial AND h.dataFinal = :dataFinal")
    Optional<HistoricoPais> findHistoricoPais(Pais pais, String dataInicial, String dataFinal);

    @Query("SELECT h FROM historico_pais h WHERE 1=1 AND h.paisId = :pais AND h.dataInicial = :data")
    Optional<HistoricoPais> findHistoricoPaisByDataUnica(Pais pais, String data);
}
