package covid.application.api.records;

public record DadosBuscaLocalidade(
        String nomeLocalidade,

        String dataInicial,

        String dataFinal
) {
}
