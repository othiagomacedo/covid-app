package covid.application.api.repository;

import covid.application.api.modelos.entidade.HistoricoPais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoPaisRepository extends JpaRepository<HistoricoPais, Long> {
}
