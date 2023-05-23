package covid.application.api.modelos.records;

public record DadosRespostaReportPais(
        String sigla,

        String data,
        String ultUpdate,
        long confirmados,
        long mortes,
        long recuperados,
        double taxaFatalidade
) {
}
