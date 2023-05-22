package covid.application.api.modelos.records;

import covid.application.api.anotations.Data;
import covid.application.api.modelos.enums.Covid;

public record DadosBuscaLocalidade(

        Covid tipoDadosCovid,

        String nomeLocalidade,

        @Data
        String dataInicial,

        @Data
        String dataFinal
) {
}
