package covid.application.api.modelos.records;

import covid.application.api.annotations.Sigla;

public record DadosRespostaReportPais(

        @Sigla
        String sigla,

        String nomePais,

        String dataInicial,

        String dataFinal,

        String ultUpdate,
        long confirmados,
        long mortes,
        long recuperados,
        double taxaFatalidade
) {
}
