package covid.application.api.modelos.records;

public record DadosRespostaBenchmark(
        long id,
        String nomeBench,
        String dataHoraBenchmark,
        String dataInicial,
        String dataFinal,
        long confirmadosTotal,
        long mortesTotal,
        long recuperadosTotal,
        long confirmadosDiferenca,
        long mortesDiferenca,
        long recuperadosDiferenca,
        DadosRespostaReportPais dadosPais1,
        DadosRespostaReportPais dadosPais2,

        boolean foiSalvo,

        String mensagem
) {
    public DadosRespostaBenchmark(long id, String nomeBench, String dataHoraBenchmark, String dataInicial, String dataFinal, long confirmadosTotal, long mortesTotal, long recuperadosTotal, long confirmadosDiferenca, long mortesDiferenca, long recuperadosDiferenca, DadosRespostaReportPais dadosPais1, DadosRespostaReportPais dadosPais2, boolean foiSalvo, String mensagem) {
        this.id = id;
        this.nomeBench = nomeBench;
        this.dataHoraBenchmark = dataHoraBenchmark;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.confirmadosTotal = confirmadosTotal;
        this.mortesTotal = mortesTotal;
        this.recuperadosTotal = recuperadosTotal;
        this.confirmadosDiferenca = confirmadosDiferenca;
        this.mortesDiferenca = mortesDiferenca;
        this.recuperadosDiferenca = recuperadosDiferenca;
        this.dadosPais1 = dadosPais1;
        this.dadosPais2 = dadosPais2;
        this.foiSalvo = foiSalvo;
        this.mensagem = mensagem;
    }


}
