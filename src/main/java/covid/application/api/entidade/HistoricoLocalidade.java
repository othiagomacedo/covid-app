package covid.application.api.entidade;

import jakarta.persistence.*;

@Entity(name = "historico_localidade")
public class HistoricoLocalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String data;

    private long confirmados;

    private long mortes;

    private long recuperados;

    @Column(name = "ult_update")
    private String ultimoUpdate;

    @Column(name = "percent_fatalidade")
    private float percentualFatalidade;
}
