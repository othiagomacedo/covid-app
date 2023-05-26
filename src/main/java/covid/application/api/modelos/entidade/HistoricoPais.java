package covid.application.api.modelos.entidade;

import covid.application.api.annotations.Data;
import covid.application.api.modelos.records.DadosBuscaPaisDatas;
import covid.application.api.modelos.records.DadosRespostaReportPais;
import jakarta.persistence.*;

@Entity(name = "historico_pais")
public class HistoricoPais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Data
    private String dataInicial;

    @Data
    private String dataFinal;

    private long confirmados;

    private long mortes;

    private long recuperados;

    @Column(name = "ultimo_update")
    private String ultimoUpdate;

    @Column(name = "percentual_fatalidade")
    private double percentualFatalidade;

    @ManyToOne
    @JoinColumn(name = "pais_id")
    Pais paisId;

    public HistoricoPais() {
    }

    public HistoricoPais(DadosRespostaReportPais dados, Pais idPais){
        this.confirmados = dados.confirmados();
        this.dataInicial = dados.dataInicial();
        this.dataFinal = dados.dataFinal();
        this.mortes = dados.mortes();
        this.recuperados = dados.recuperados();
        this.ultimoUpdate = dados.ultUpdate();
        this.percentualFatalidade = dados.taxaFatalidade();
        this.paisId = idPais;
    }

    public HistoricoPais(DadosBuscaPaisDatas dataBuscas, DadosRespostaReportPais dados, Pais idPais){
        this.dataInicial = dataBuscas.dataInicial();
        this.dataFinal = dataBuscas.dataFinal();
        this.confirmados = dados.confirmados();
        this.mortes = dados.mortes();
        this.recuperados = dados.recuperados();
        this.ultimoUpdate = dados.ultUpdate();
        this.percentualFatalidade = dados.taxaFatalidade();
        this.paisId = idPais;
    }


    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public long getConfirmados() {
        return confirmados;
    }

    public void setConfirmados(long confirmados) {
        this.confirmados = confirmados;
    }

    public long getMortes() {
        return mortes;
    }

    public void setMortes(long mortes) {
        this.mortes = mortes;
    }

    public long getRecuperados() {
        return recuperados;
    }

    public void setRecuperados(long recuperados) {
        this.recuperados = recuperados;
    }

    public String getUltimoUpdate() {
        return ultimoUpdate;
    }

    public void setUltimoUpdate(String ultimoUpdate) {
        this.ultimoUpdate = ultimoUpdate;
    }

    public double getPercentualFatalidade() {
        return percentualFatalidade;
    }

    public void setPercentualFatalidade(double percentualFatalidade) {
        this.percentualFatalidade = percentualFatalidade;
    }

    public String getPaisSigla() {
        return paisId.getSigla();
    }

    public long getId() {
        return id;
    }
}
