package covid.application.api.service;

import covid.application.api.external.Requests;
import covid.application.api.modelos.entidade.*;
import covid.application.api.modelos.enums.Requisicao;
import covid.application.api.modelos.records.*;
import covid.application.api.repository.*;
import covid.application.api.util.CriarRecords;
import covid.application.api.util.Datas;
import covid.application.api.util.Verificar;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

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

    public ResponseEntity obterBenchPeloID(long id){
        try {
            Benchmark benchResp = bench.findById(id).get();
            DadosRespostaBenchmark dados = CriarRecords.montarDadosRespostaBenchmark(benchResp);
            return ResponseEntity.ok(dados);
        } catch (Exception e) {
            LOG.error("Benchmark de id " + id + " não existe", e);
            LOG.info("Vou devolver uma resposta ao cliente que benchmark de id " + id + " não existe");
            return ResponseEntity.badRequest().body("Bench de id " + id + " não existe");
        }
    }

    public ResponseEntity obterBenchmark(DadosBuscaBenchmark dadosBusca) throws Exception {
        String nomeBench = dadosBusca.nomeBench();
        String paisSigla1 = dadosBusca.paisSigla1();
        String paisSigla2 = dadosBusca.paisSigla2();
        String dataInicial = dadosBusca.dataInicial();
        String dataFinal = dadosBusca.dataFinal();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //Verifica se existe bench com mesmo nome
        if (!seExisteBenchmarkComNome(nomeBench)) {
            LOG.info("Existe um benchmark salvo com este nome: " + nomeBench + ". Vou enviar o mesmo ao cliente.");
            Benchmark benchs = bench.findBenchmarkByNome(nomeBench).get();
            return ResponseEntity.ok(CriarRecords.montarDadosRespostaBenchmark(benchs));
        }

        //Verifica se siglas estão corretas
        if (!seSiglaTamanhoCorreto(paisSigla1)) {
            return ResponseEntity.badRequest().body("Pais sigla " + paisSigla1 + " não está correta.");
        }

        if (!seSiglaTamanhoCorreto(paisSigla2)) {
            return ResponseEntity.badRequest().body("Pais sigla " + paisSigla2 + " não está correta.");
        }

        //Verifica as Datas
        if (!Datas.isCorreto(dataInicial)){
            LOG.error("Data "+dataInicial+" não está correta. o Formato correto é yyyy-mm-dd");
            return ResponseEntity.badRequest().body("Data "+dataInicial+" não está correta. o Formato correto é yyyy-mm-dd");
        }
        if (!Datas.isCorreto(dataFinal)){
            LOG.error("Data "+dataInicial+" não está correta. o Formato correto é yyyy-mm-dd");
            return ResponseEntity.badRequest().body("Data "+dataFinal+" não está correta. o Formato correto é yyyy-mm-dd");
        }

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
            //Montar benchmark e persistir no banco de dados local
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
                    0,
                    dadosBusca.nomeBench(),
                    sdf.format(date),
                    dataInicial,
                    dataFinal,
                    confirmadosDiferenca < 0 ? confirmadosDiferenca * (-1L) : confirmadosDiferenca,
                    mortesDiferenca < 0 ? mortesDiferenca * (-1L) : mortesDiferenca,
                    recuperadosDiferenca < 0 ? recuperadosDiferenca * (-1L) : recuperadosDiferenca,
                    dadosPais1,
                    dadosPais2,
                    true,
                    ""
            );

            //persistir benchmark
            try {
                benchmark = saveBenchmark(benchmark, dadosPais1, dadosPais2);
            } catch (Exception ignored) {
            }

            //enviar benchmark ao cliente
            LOG.info("Enviado benchmark ao cliente");
            return ResponseEntity.ok(benchmark);
        }

    }

    public ResponseEntity obterTodosBenchmarks() {
        LOG.info("Obtendo todos benchmarks salvos anteriormente");
        List<Benchmark> lista = bench.findAll();
        LOG.info("Enviado a lista completa. Total da lista é " + lista.size());
        return ResponseEntity.ok(lista);
    }

    public ResponseEntity deletarBenchPeloNome(DadosExcluirBench benchmark) throws Exception {
        try {
            String nome = benchmark.nomeBench();
            LOG.info("Irei tentar realizar o delete do benchmark " + nome);
            //Verificar se existe bench pelo nome
            if (!seExisteBenchmarkComNome(nome)) {
                Benchmark be = bench.findBenchmarkByNome(nome).get();
                LOG.info("Existe bench de nome " + nome + " salvo localmente, logo vou iniciar processo de delete do mesmo");
                bench.delete(be);
                LOG.info("Benchmark de nome " + nome + " deletado.");
                return ResponseEntity.ok("Benchmark de nome " + nome + " deletado.");
            } else {
                LOG.info("Benchmark de nome " + nome + " não existe, logo, não realizarei um delete");
                return ResponseEntity.badRequest().body("Benchmark de nome " + nome + " não existe, logo, não realizarei um delete");
            }
        } catch (Exception e) {
            LOG.error("Erro ao tentar deletar benchmark.", e);
            return ResponseEntity.badRequest().body("Erro em deletar benchmark, por favor tente novamente.");
        }
    }

    public ResponseEntity deletarBenchPeloId(DadosExcluirBench benchmark) {
        try {
            long id = benchmark.id();
            LOG.info("Irei tentar realizar o delete do benchmark id " + id);
            if (!seExisteBenchmarkPeloID(id)) {
                LOG.info("Identificado o benchmark no banco local, logo, irei tentar realizar um delete.");
                Benchmark be = bench.findById(id).get();
                bench.delete(be);
                LOG.info("Benchmark deletado com sucesso.");
                return ResponseEntity.ok("Benchmark de id " + id + "deletado com sucesso.");
            } else{
                return ResponseEntity.badRequest().body("Benhchmark de id " + id +" não existe.");
            }
        } catch (Exception e) {
            LOG.error("Erro ao tentar deletar benchmark.", e);
        }
        return ResponseEntity.badRequest().body("Erro em deletar benchmark, por favor tente novamente.");
    }

    public ResponseEntity editarBenchmark(HttpServletRequest request) throws Exception {
        LOG.info("Inicio da edição do benchmark");
        //Desseralizar o JSON recebido
        StringBuilder stringBuilder = new StringBuilder();
        String json;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))){
            LOG.info("Vou buscar ler e obter JSON da requisicao.");
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }

            // Aqui está o JSON como uma string
            json = stringBuilder.toString();

        } catch (IOException e) {
            LOG.error("Não foi possível obter o JSON da requisição.",e);
            throw e;
        }

        return ResponseEntity.badRequest().body("Erro ao editar benchmark");
    }

    /**
     * VERIFICAÇÕES E UTILS DA CLASSE
     */

    public DadosRespostaBenchmark saveBenchmark(DadosRespostaBenchmark benchmark, DadosRespostaReportPais pais1, DadosRespostaReportPais pais2) throws Exception {
        DadosRespostaBenchmark dados;
        try {
            LOG.info("Início para persistir novo benchmark", benchmark);
            //Obter os países
            String siglaPais1 = pais1.sigla();
            String siglaPais2 = pais2.sigla();

            //Verifica se tem países, se não busca na API e persiste no banco
            Pais paisAux1 = obterPaisSeExisteOuNao(siglaPais1);
            Pais paisAux2 = obterPaisSeExisteOuNao(siglaPais2);

            //Verifica se datas estão corretas
            if(!Datas.isCorreto(pais1.dataInicial()) || !Datas.isCorreto(pais1.dataFinal())){
                throw new Exception("Não vou persistir esse Benchmark porque o país de "+siglaPais1+" não tem informações de data, provavelmente não tem informações sobre esse país");
            }

            if(!Datas.isCorreto(pais2.dataInicial()) || !Datas.isCorreto(pais2.dataFinal())){
                throw new Exception("Não vou persistir esse Benchmark porque o país de "+siglaPais1+" não tem informações de data, provavelmente não tem informações sobre esse país");
            }

            //Salvar os dois historicos pais
            HistoricoPais paisSave1;
            HistoricoPais paisSave2;
            if (seExisteHistoricoSalvoParaPais(paisAux1, pais1.dataInicial(),pais1.dataFinal())){
                LOG.info("Não existe um histórico para o país "+siglaPais1+" , então vou salvar o mesmo no banco local.");
                paisSave1 = historico.save(new HistoricoPais(pais1, paisAux1));
                LOG.info("Historico do país "+ siglaPais1+ " salvo com sucesso e disponível para este Benchmark");
            } else {
                LOG.info("Já existe um histórico para o país "+siglaPais1+" de mesma data, logo vou usar o mesmo.");
                paisSave1 = historico.findHistoricoPais(paisAux1, pais1.dataInicial(),pais1.dataFinal()).get();
            }

            if (seExisteHistoricoSalvoParaPais(paisAux2, pais2.dataInicial(),pais2.dataFinal())){
                LOG.info("Não existe um histórico para o país "+siglaPais2+" , então vou salvar o mesmo no banco local.");
                paisSave2 = historico.save(new HistoricoPais(pais2, paisAux2));
                LOG.info("Historico do país "+ siglaPais2+ " salvo com sucesso e disponível para este Benchmark");
            } else {
                LOG.info("Já existe um histórico para o país "+siglaPais2+" de mesma data, logo vou usar o mesmo.");
                paisSave2 = historico.findHistoricoPais(paisAux2, pais2.dataInicial(),pais2.dataFinal()).get();
            }

            //Salvar o benchmark
            Benchmark benchmarkSave = new Benchmark(
                    benchmark.nomeBench(),
                    paisSave1,
                    paisSave2,
                    benchmark.dataHoraBenchmark(),
                    benchmark.dataInicial(),
                    benchmark.dataFinal(),
                    benchmark.confirmadosDiferenca(),
                    benchmark.mortesDiferenca(),
                    benchmark.recuperadosDiferenca()
            );

            LOG.info("Benchmark montado. Vou tentar persistir o mesmo.");
            Benchmark benchm = bench.save(benchmarkSave);

            DadosRespostaBenchmark dadosResposta = CriarRecords.montarDadosRespostaBenchmark(benchm);
            LOG.info("Benchmark de nome " + benchmark.nomeBench() + " salvo.");
            return dadosResposta;
        } catch (Exception e) {
            LOG.error("Houve um erro ao tentar persistir o benchmark.", e);
            dados = new DadosRespostaBenchmark(
                    benchmark.id(),
                    benchmark.nomeBench(),
                    benchmark.dataHoraBenchmark(),
                    benchmark.dataInicial(),
                    benchmark.dataFinal(),
                    benchmark.confirmadosDiferenca(),
                    benchmark.mortesDiferenca(),
                    benchmark.recuperadosDiferenca(),
                    benchmark.dadosPais1(),
                    benchmark.dadosPais2(),
                    false,
                    e.getMessage()
            );
            return dados;
        }
    }

    public Pais obterPaisSeExisteOuNao(String sigla) throws Exception {
        Pais paisAux2;
        boolean achouAPI = false;
        try {
            paisAux2 = pais.findBySigla(sigla).get();
            return paisAux2;
        } catch (Exception e) {
            LOG.info("País do benchmark de sigla " + sigla + " não existe no banco, logo vou buscar na APi e persistir o mesmo.");
            //Obter nome e sigla e persistir no banco
            String retorno = Requests.realizarRequest(Requisicao.OBTER_TODOS_PAISES_E_SIGLAS.get());
            List<DadosPaisesSigla> listaDados = CriarRecords.montarRecordPaisesSigla(retorno);
            for (DadosPaisesSigla dadosP : listaDados) {
                if (dadosP.sigla().equalsIgnoreCase(sigla) || dadosP.sigla().contains(sigla) || dadosP.nome().contains(sigla)) {
                    LOG.info("Pais encontrado na API Externa! " + sigla + ". O mesmo será persistido no banco local e enviado ao Cliente");
                    paisAux2 = pais.save(new Pais(dadosP));
                    achouAPI = true;
                    return paisAux2;
                }
            }

            if (!achouAPI) {
                throw new Exception("País com sigla " + sigla + " não existe no banco e nem na API externa.");
            }
        }
        return new Pais(sigla, sigla);
    }

    public boolean seExisteHistoricoSalvoParaPais(Pais pais, String dataInicial, String dataFinal) throws Exception {
        try {
            var hpAux = historico.findHistoricoPais(pais,dataInicial,dataFinal);
            return hpAux.isEmpty();
        } catch (Exception e) {
            LOG.error("Não foi possível verificar se existe historico de País");
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
        } catch (NoSuchElementException nse) {
            LOG.info("Não existe um benchmark no banco. Liberado para criar novo.", nse);
            return false;
        }
    }

    public boolean seExisteBenchmarkComNome(String nomeBench) {
        try {
            var benchs = bench.findBenchmarkByNome(nomeBench);
            return benchs.isEmpty();
        } catch (Exception e) {
            LOG.error("Houve um erro ao buscar Benchmark pelo nome");
        }
        return false;
    }

    public boolean seExisteBenchmarkPeloID(long id) {
        try {
            var be = bench.findById(id);
            return be.isEmpty();
        } catch (Exception e) {
            LOG.error("Houve um erro ao buscar Benchmark pelo nome");
        }
        return false;
    }

}
