package covid.application.api.service;

import covid.application.api.external.Requests;
import covid.application.api.modelos.entidade.Benchmark;
import covid.application.api.modelos.entidade.HistoricoPais;
import covid.application.api.modelos.entidade.Pais;
import covid.application.api.modelos.enums.Requisicao;
import covid.application.api.modelos.records.DadosBuscaBenchmark;
import covid.application.api.modelos.records.DadosRespostaBenchmark;
import covid.application.api.modelos.records.DadosRespostaReportPais;
import covid.application.api.repository.*;
import covid.application.api.util.CriarRecords;
import covid.application.api.util.Verificar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class BenchMarkService {

    @Autowired
    private BenchmarkRepository bench;

    @Autowired
    private HistoricoPaisRepository historico;

    @Autowired
    private PaisRepository pais;

    Verificar verifica;

    private static Logger LOG = LoggerFactory.getLogger(BenchMarkService.class);

    public BenchMarkService() {
        verifica = new Verificar();
    }

    public ResponseEntity obterBenchmark(DadosBuscaBenchmark dadosBusca) throws Exception {
        String nomeBench = dadosBusca.nomeBench();
        String paisSigla1 = dadosBusca.paisSigla1();
        String paisSigla2 = dadosBusca.paisSigla2();
        String dataInicial = dadosBusca.dataInicial();
        String dataFinal = dadosBusca.dataFinal();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //Verifica se existe bench com mesmo nome
        if(!seExisteBenchmarkComNome(nomeBench)){
            LOG.error("Existe um benchmark salvo com este nome: "+nomeBench+". Por favor use outro nome");
            return ResponseEntity.badRequest().body("Existe um benchmark salvo com este nome: "+nomeBench+". Por favor use outro nome");
        }

        //Verifica se siglas estão corretas
        if (!seSiglaTamanhoCorreto(paisSigla1)) {
            return ResponseEntity.badRequest().body("Pais sigla " + paisSigla1 + " não está correta.");
        }

        if (!seSiglaTamanhoCorreto(paisSigla2)) {
            return ResponseEntity.badRequest().body("Pais sigla " + paisSigla2 + " não está correta.");
        }

        //Verifica se já existe um país com essa sigla
//        if (!sePaisExisteSalvoLocalPelaSigla(paisSigla1)) {
//            return ResponseEntity.badRequest().body("País de Sigla " + paisSigla1 + " não encontrado");
//        }
//
//        if (!sePaisExisteSalvoLocalPelaSigla(paisSigla2)) {
//            return ResponseEntity.badRequest().body("País de Sigla " + paisSigla2 + " não encontrado");
//        }

        //Verifica se existe algum benchmark igual ao requisitado
        if (seExisteBenchmark(dadosBusca)) {
            LOG.info("Já existe um Benchmark igual ao requisitado, logo enviarei o mesmo ao cliente.");
            Pais pais1 = pais.findBySigla(paisSigla1).get();
            Pais pais2 = pais.findBySigla(paisSigla2).get();
            HistoricoPais historicoPais1 = historico.findHistoricoPais(pais1, dataInicial, dataFinal).get();
            HistoricoPais historicoPais2 = historico.findHistoricoPais(pais2, dataInicial, dataFinal).get();
            Benchmark benchs1 = bench.findHistoricoBenchmark(historicoPais1, historicoPais2, dataInicial, dataFinal).get();
            LOG.info("Envio de Benchmark realizado com sucesso.");
            return ResponseEntity.ok().body(benchs1);
        } else {
            LOG.info("Não há um benchmark previamente salvo, logo gerarei um e enviarei ao cliente.");
            String url = Requisicao.REPORT_TOTAL_DIA_PAIS_POR_DATA_SIGLA.get();

            String url1 = url.replace("{DATA}", dadosBusca.dataInicial()).replace("{SIGLA}", paisSigla1);
            String url2 = url.replace("{DATA}", dadosBusca.dataFinal()).replace("{SIGLA}", paisSigla2);

            String jsonPaisResp1 = Requests.realizarRequest(url1);
            String jsonPaisResp2 = Requests.realizarRequest(url2);

            LOG.info("Obtido os dois históricos dos países do benchmark, continuarei montando o benchmark");

            DadosRespostaReportPais dadosPais1 = CriarRecords.montarRecordRespostaDadosPais(paisSigla1, jsonPaisResp1, dataFinal);
            DadosRespostaReportPais dadosPais2 = CriarRecords.montarRecordRespostaDadosPais(paisSigla2, jsonPaisResp2, dataFinal);

            long confirmadosDiferenca = dadosPais1.confirmados() - dadosPais2.confirmados();
            long mortesDiferenca = dadosPais1.mortes() - dadosPais2.mortes();
            long recuperadosDiferenca = dadosPais1.recuperados() - dadosPais2.recuperados();

            DadosRespostaBenchmark benchmark = new DadosRespostaBenchmark(
                    dadosBusca.nomeBench(),
                    sdf.format(date),
                    dataInicial,
                    dataFinal,
                    confirmadosDiferenca,
                    mortesDiferenca,
                    recuperadosDiferenca,
                    dadosPais1,
                    dadosPais2
            );

            LOG.info("Benchmark montado. Vou tentar persistir o mesmo.");

            //persistir benchmark
            saveBenchmark(benchmark,dadosPais1,dadosPais2);

            //enviar benchmark ao cliente
            LOG.info("Enviado benchmark ao cliente");
            return ResponseEntity.ok(benchmark);
        }

    }


    /**
     * VERIFICAÇÕES E UTILS DA CLASSE
     */

    public void saveBenchmark(DadosRespostaBenchmark benchmark, DadosRespostaReportPais pais1, DadosRespostaReportPais pais2) throws Exception {
        try {
            LOG.info("Início para persistir novo benchmark",benchmark);
            //Obter os países
            String siglaPais1 = pais1.sigla();
            String siglaPais2 = pais2.sigla();

            Pais paisAux1 = pais.findBySigla(siglaPais1).get();
            Pais paisAux2 = pais.findBySigla(siglaPais2).get();

            //Salvar os dois historicos pais
            HistoricoPais paisSave1 = historico.save(new HistoricoPais(pais1,paisAux1));
            HistoricoPais paisSave2 = historico.save(new HistoricoPais(pais2,paisAux2));

            //Salvar o benchmark
            Benchmark benchmarkSave = new Benchmark(
                    benchmark.nomeBench(),
                    paisSave1,
                    paisSave2,
                    benchmark.dataHoraBenchmark(),
                    benchmark.dataInicial(),
                    benchmark.dataFinal()
            );

            bench.save(benchmarkSave);
            LOG.info("Benchmark de nome "+benchmark.nomeBench()+" salvo.");
        } catch (Exception e) {
            LOG.error("Houve um erro ao tentar persistir o benchmark.",e);
        }
    }

    public boolean sePaisExisteSalvoLocalPelaSigla(String sigla) {
        try {
            var paisBuscado = pais.findBySigla(sigla);
            if (!paisBuscado.isEmpty()) {
                LOG.info("País de sigla " + sigla + " encontrado.");
                return true;
            }
        } catch (Exception e) {
            LOG.warn("Não foi possível buscar país com sigla " + sigla, e);
        }
        return false;
    }

    public boolean seSiglaTamanhoCorreto(String sigla) {
        if (sigla.length() != 3) {
            LOG.error("A Sigla " + sigla + " está incorreta. Siglas devem conter 3 caracteres.");
            return false;
        }
        return true;
    }

    public boolean seExisteBenchmark(DadosBuscaBenchmark dados) {
        try {
            String dataInicial = dados.dataInicial();
            String dataFinal = dados.dataFinal();
            Pais pais1 = pais.findBySigla(dados.paisSigla1()).get();
            Pais pais2 = pais.findBySigla(dados.paisSigla2()).get();
            HistoricoPais historicoPais1 = historico.findHistoricoPais(pais1, dataInicial, dataFinal).get();
            HistoricoPais historicoPais2 = historico.findHistoricoPais(pais2, dataInicial, dataFinal).get();

            Benchmark benchs = bench.findHistoricoBenchmark(historicoPais1, historicoPais2, dataInicial, dataFinal).get();
            return benchs == null;
        } catch (Exception e) {
            LOG.error("Houve um erro ao tentar validar se historico benchmark existe. ", e);
            return false;
        }
    }

    public boolean seExisteBenchmarkComNome(String nomeBench){
        try {
            var benchs = bench.findBenchmarkByNome(nomeBench);
            return benchs.isEmpty();
        }catch (Exception e) {
            LOG.error("Houve um erro ao buscar Benchmark pelo nome");
        }
        return false;
    }

}
