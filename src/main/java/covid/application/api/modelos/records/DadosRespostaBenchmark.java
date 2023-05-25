package covid.application.api.modelos.records;

public record DadosRespostaBenchmark(
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
