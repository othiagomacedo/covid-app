package covid.application.api.modelos.records;

import covid.application.api.anotations.Data;
import covid.application.api.anotations.Sigla;

public record DadosBuscaPaisDatas(
        @Sigla
        String sigla,

        @Data
        String dataInicial,

        @Data
        String dataFinal
) {
}
