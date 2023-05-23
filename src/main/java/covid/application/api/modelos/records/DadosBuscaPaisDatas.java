package covid.application.api.modelos.records;

import covid.application.api.anotations.Data;

public record DadosBuscaPaisDatas(
        String sigla,

        @Data
        String dataInicial,

        @Data
        String dataFinal
) {
}
