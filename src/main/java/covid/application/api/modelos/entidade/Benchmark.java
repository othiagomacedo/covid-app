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

    @Column(name = "confirmados_diferenca")
    long confirmadosDiferenca;

    @Column(name = "mortes_diferenca")
    long mortesDiferenca;

    @Column(name = "recuperados_diferenca")
    long recuperadosDiferenca;

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

    public Benchmark(String nomeHistorico, HistoricoPais historicoPais1, HistoricoPais historicoPais2, String dataHistorico, String dataIncial, String dataFinal, long confirmadosDiferenca, long mortesDiferenca, long recuperadosDiferenca) {
        this.nomeHistorico = nomeHistorico;
        this.historicoPais1 = historicoPais1;
        this.historicoPais2 = historicoPais2;
        this.dataHistorico = dataHistorico;
        this.dataIncial = dataIncial;
        this.dataFinal = dataFinal;
        this.confirmadosDiferenca = confirmadosDiferenca;
        this.mortesDiferenca = mortesDiferenca;
        this.recuperadosDiferenca = recuperadosDiferenca;
    }

    public String getNomeHistorico() {
        return nomeHistorico;
    }

    public void setNomeHistorico(String nomeHistorico) {
        this.nomeHistorico = nomeHistorico;
    }

    public HistoricoPais getHistoricoPais1() {
        return historicoPais1;
    }

    public void setHistoricoPais1(HistoricoPais historicoPais1) {
        this.historicoPais1 = historicoPais1;
    }

    public HistoricoPais getHistoricoPais2() {
        return historicoPais2;
    }

    public void setHistoricoPais2(HistoricoPais historicoPais2) {
        this.historicoPais2 = historicoPais2;
    }

    public String getDataHistorico() {
        return dataHistorico;
    }

    public void setDataHistorico(String dataHistorico) {
        this.dataHistorico = dataHistorico;
    }

    public String getDataIncial() {
        return dataIncial;
    }

    public void setDataIncial(String dataIncial) {
        this.dataIncial = dataIncial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getConfirmadosDiferenca() {
        return confirmadosDiferenca;
    }

    public void setConfirmadosDiferenca(long confirmadosDiferenca) {
        this.confirmadosDiferenca = confirmadosDiferenca;
    }

    public long getMortesDiferenca() {
        return mortesDiferenca;
    }

    public void setMortesDiferenca(long mortesDiferenca) {
        this.mortesDiferenca = mortesDiferenca;
    }

    public long getRecuperadosDiferenca() {
        return recuperadosDiferenca;
    }

    public void setRecuperadosDiferenca(long recuperadosDiferenca) {
        this.recuperadosDiferenca = recuperadosDiferenca;
    }
}
