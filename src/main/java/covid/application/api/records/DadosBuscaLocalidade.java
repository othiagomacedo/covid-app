package covid.application.api.records;

import covid.application.api.anotations.Data;

public record DadosBuscaLocalidade(
        String nomeLocalidade,

        @Data
        String dataInicial,

        @Data
        String dataFinal
) {
}
