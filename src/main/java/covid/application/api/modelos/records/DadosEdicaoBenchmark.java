package covid.application.api.modelos.records;

public record DadosEdicaoBenchmark(
        long id,
        String nomeBench,
        String dataHoraBenchmark,
        String dataInicial,
        String dataFinal,
        long confirmadosDiferenca,
        long mortesDiferenca,
        long recuperadosDiferenca,
        DadosRespostaReportPais dadosPais1,
        DadosRespostaReportPais dadosPais2
) {
}
