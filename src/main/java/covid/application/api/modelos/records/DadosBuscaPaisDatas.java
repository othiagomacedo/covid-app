package covid.application.api.modelos.records;

import covid.application.api.annotations.Data;
import covid.application.api.annotations.Sigla;

public record DadosBuscaPaisDatas(
        @Sigla
        String sigla,

        @Data
        String dataInicial,

        @Data
        String dataFinal
) {
}
