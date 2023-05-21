package covid.application.api.repository;

import covid.application.api.modelos.entidade.HistoricoLocalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoLocalidadeRepository extends JpaRepository<HistoricoLocalidade, Long> {
}
