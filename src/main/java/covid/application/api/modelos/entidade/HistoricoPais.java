package covid.application.api.modelos.entidade;

import covid.application.api.anotations.Data;
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

    @Column(name = "percent_fatalidade")
    private float percentualFatalidade;

    @ManyToOne
    @JoinColumn(name = "pais_id")
    Pais paisId;

    public HistoricoPais() {
    }


}
