package covid.application.api.modelos.records;

import covid.application.api.anotations.Sigla;

public record DadosRespostaReportPais(

        @Sigla
        String sigla,

        String dataInicial,

        String dataFinal,

        String ultUpdate,
        long confirmados,
        long mortes,
        long recuperados,
        double taxaFatalidade
) {
}
