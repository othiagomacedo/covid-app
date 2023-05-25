package covid.application.api.modelos.entidade;

import covid.application.api.modelos.records.DadosRespostaBenchmark;
import jakarta.persistence.*;

@Entity(name = "historico_benchmark")
@Table(name = "historico_benchmark")
public class Benchmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "nome_historico")
    String nomeHistorico;

    @ManyToOne
    @JoinColumn(name = "historico_pais1_id")
    HistoricoPais historicoPais1;

    @ManyToOne
    @JoinColumn(name = "historico_pais2_id")
    HistoricoPais historicoPais2;

    @Column(name = "data_historico")
    String dataHistorico;

    @Column(name = "data_inicial")
    String dataIncial;

    @Column(name = "data_final")
    String dataFinal;

    public Benchmark() {}

    public Benchmark(String nomeHistorico, HistoricoPais historicoPais1, HistoricoPais historicoPais2, String dataHistorico, String dataIncial, String dataFinal) {
        this.nomeHistorico = nomeHistorico;
        this.historicoPais1 = historicoPais1;
        this.historicoPais2 = historicoPais2;
        this.dataHistorico = dataHistorico;
        this.dataIncial = dataIncial;
        this.dataFinal = dataFinal;
    }

    public Benchmark(DadosRespostaBenchmark bench, HistoricoPais historicoPais1, HistoricoPais historicoPais2){
        this.nomeHistorico = bench.nomeBench();
        this.historicoPais1 = historicoPais1;
        this.historicoPais2 = historicoPais2;
        this.dataHistorico = bench.dataHoraBenchmark();
        this.dataIncial = bench.dataInicial();
        this.dataFinal = bench.dataFinal();
    }
}
