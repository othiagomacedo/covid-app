package covid.application.api.repository;

import covid.application.api.modelos.entidade.HistoricoBenchmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoBenchmarkRepository extends JpaRepository<HistoricoBenchmark, Long> {
}
