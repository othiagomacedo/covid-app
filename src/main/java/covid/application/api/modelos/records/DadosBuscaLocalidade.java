package covid.application.api.modelos.records;

import covid.application.api.anotations.Data;
import covid.application.api.modelos.enums.TipoLocalidade;

public record DadosBuscaLocalidade(

        TipoLocalidade tipoLocalidade,
        String nomeLocalidade,

        @Data
        String dataInicial,

        @Data
        String dataFinal
) {
}
