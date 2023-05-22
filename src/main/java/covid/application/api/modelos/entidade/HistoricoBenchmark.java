package covid.application.api.modelos.entidade;

import covid.application.api.anotations.DataHora;
import jakarta.persistence.*;

@Entity
public class HistoricoBenchmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn(name = "historico_pais1_id")
    HistoricoPais historicoPais1;

    @ManyToOne
    @JoinColumn(name = "historico_pais2_id")
    HistoricoPais historicoPais2;

    @DataHora
    String dataHistorico;

    public HistoricoBenchmark() {}
}
