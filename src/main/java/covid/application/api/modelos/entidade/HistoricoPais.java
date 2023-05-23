package covid.application.api.modelos.entidade;

import covid.application.api.anotations.Data;
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

    @Data
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


}
