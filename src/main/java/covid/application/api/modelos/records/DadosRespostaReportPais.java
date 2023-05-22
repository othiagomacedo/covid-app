package covid.application.api.modelos.records;

public record DadosRespostaReportPais(
        String data,
        String ultUpdate,
        int confirmados,
        int confirmadosDiff,
        int mortes,
        int mortesDiff,
        int recuperados,
        int recuperadosDiff,
        int ativos,
        int ativosDiff,
        double taxaFatalidade
) {
}
