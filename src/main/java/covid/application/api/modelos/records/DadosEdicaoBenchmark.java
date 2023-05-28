package covid.application.api.modelos.records;

public record DadosEdicaoBenchmark(
        long id,
        String nomeBench,
        String siglaPais1,
        String siglaPais2,
        String dataInicial,
        String dataFinal
) {
}
