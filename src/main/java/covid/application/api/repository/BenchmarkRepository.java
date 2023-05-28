package covid.application.api.repository;

import covid.application.api.modelos.entidade.Benchmark;
import covid.application.api.modelos.entidade.HistoricoPais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BenchmarkRepository extends JpaRepository<Benchmark, Long> {

    @Query("SELECT b FROM historico_benchmark b WHERE 1=1 AND b.historicoPais1 = :historicoPais1 AND " +
            "b.historicoPais2 = :historicoPais2 AND " +
            "b.dataIncial = :dataIncial AND " +
            "b.dataFinal = :dataFinal")
    Optional<Benchmark> findHistoricoBenchmark(HistoricoPais historicoPais1,
                                               HistoricoPais historicoPais2,
                                               String dataIncial,
                                               String dataFinal);

    @Query("SELECT b FROM historico_benchmark b WHERE 1=1 and b.nomeHistorico = :nome")
    Optional<Benchmark> findBenchmarkByNome(String nome);

}
