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
        if (!Datas.isCorreto(dataInicial)) {
            LOG.error("Data " + dataInicial + " não está correta. o Formato correto é yyyy-mm-dd");
            return ResponseEntity.badRequest().body("Data " + dataInicial + " não está correta. o Formato correto é yyyy-mm-dd");
        }
        if (!Datas.isCorreto(dataFinal)) {
            LOG.error("Data " + dataInicial + " não está correta. o Formato correto é yyyy-mm-dd");
            return ResponseEntity.badRequest().body("Data " + dataFinal + " não está correta. o Formato correto é yyyy-mm-dd");
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
            
            //Verificar se já existe o histórico dos países no banco salvo com mesmo tipo
            LOG.info("Vou verificar se já existe o histórico dos países");
            
            String jsonPaisResp1 = null;
            String jsonPaisResp2 = null;

            DadosRespostaReportPais dadosPais1 = obterHistoricoPelaSiglaDatas(paisSigla1, dataInicial, dataFinal);
            DadosRespostaReportPais dadosPais2 = obterHistoricoPelaSiglaDatas(paisSigla2, dataInicial, dataFinal);   

            LOG.info("Obtido os dois históricos dos países do benchmark, continuarei montando o benchmark");

            long totConfirmados = dadosPais1.confirmados() + dadosPais2.confirmados();
            long totMortes = dadosPais1.mortes() + dadosPais2.mortes();
            long totRecuperados = dadosPais1.recuperados() + dadosPais2.recuperados();

            long confirmadosDiferenca = dadosPais1.confirmados() - dadosPais2.confirmados();
            long mortesDiferenca = dadosPais1.mortes() - dadosPais2.mortes();
            long recuperadosDiferenca = dadosPais1.recuperados() - dadosPais2.recuperados();

            DadosRespostaBenchmark benchmark = new DadosRespostaBenchmark(
                    0,
                    dadosBusca.nomeBench(),
                    sdf.format(date),
                    dataInicial,
                    dataFinal,
                    totConfirmados,
                    totMortes,
                    totRecuperados,
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
                return ResponseEntity.ok("Benchmark de id " + id + " deletado com sucesso.");
            } else {
                return ResponseEntity.badRequest().body("Benhchmark de id " + id + " não existe.");
            }
        } catch (Exception e) {
            LOG.error("Erro ao tentar deletar benchmark.", e);
        }
        return ResponseEntity.badRequest().body("Erro em deletar benchmark, por favor tente novamente.");
    }

    public ResponseEntity editarBenchmark(DadosEdicaoBenchmark edit) throws Exception {
        LOG.info("Inicio da edição do benchmark");
        long id = edit.id();
        String nomeBench = edit.nomeBench();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //Verifica se existe Bench pelo id
            if (!seExisteBenchmarkPeloID(id)) {
                LOG.info("Identificado o benchmark no banco. Vou dar sequencia na edição.");
            } else {
                return ResponseEntity.badRequest().body("Benchmark de ID " + id + " não existe no banco. Se preferir, crie um com os mesmos dados de edição.");
            }

            //Verifica se existe pais com a sigla, se nao realiza a persistencia do mesmo
            String siglaPais1 = edit.siglaPais1();
            String siglaPais2 = edit.siglaPais2();
            String dataInicial = edit.dataInicial();
            String dataFinal = edit.dataFinal();

            Pais paisAux1 = obterPaisSeExisteOuNao(siglaPais1);
            Pais paisAux2 = obterPaisSeExisteOuNao(siglaPais2);

            //Verifica se tem historico para o país já solicitado, se não cria um historico e usa na edição
            HistoricoPais paisSave1;
            HistoricoPais paisSave2;

            if (seExisteHistoricoSalvoParaPais(paisAux1, dataInicial, dataFinal)) {
                LOG.info("Não existe um histórico para o país " + siglaPais1 + " , então vou gerar um e salvar o mesmo no banco local.");
                LOG.info("Vou obter o historico do país de sigla " + siglaPais1 + "na API.");
                DadosRespostaReportPais dadosRes1 = CriarRecords.obterHistoricoPaisDaAPI(paisAux1, dataInicial, dataFinal);
                paisSave1 = historico.save(new HistoricoPais(dadosRes1, paisAux1));
                LOG.info("Historico do país " + siglaPais1 + " salvo com sucesso e disponível para este Benchmark editado");
            } else {
                LOG.info("Já existe um histórico para o país " + siglaPais1 + " de mesma data, logo vou usar o mesmo.");
                paisSave1 = historico.findHistoricoPais(paisAux1, dataInicial, dataFinal).get();
            }

            if (seExisteHistoricoSalvoParaPais(paisAux2, dataInicial, dataFinal)) {
                LOG.info("Não existe um histórico para o país " + siglaPais2 + " , então vou gerar um e salvar o mesmo no banco local.");
                LOG.info("Vou obter o historico do país de sigla " + siglaPais2 + "na API.");
                DadosRespostaReportPais dadosRes2 = CriarRecords.obterHistoricoPaisDaAPI(paisAux2, dataInicial, dataFinal);
                paisSave2 = historico.save(new HistoricoPais(dadosRes2, paisAux2));
                LOG.info("Historico do país " + siglaPais2 + " salvo com sucesso e disponível para este Benchmark");
            } else {
                LOG.info("Já existe um histórico para o país " + siglaPais2 + " de mesma data, logo vou usar o mesmo.");
                paisSave2 = historico.findHistoricoPais(paisAux2, dataInicial, dataFinal).get();
            }

            //Obter o benchmark solicitado para a edicao
            Benchmark ben = bench.findById(edit.id()).get();

            long confirmadosDiferenca = paisSave2.getConfirmados() - paisSave1.getConfirmados();
            long mortesDiferenca = paisSave2.getMortes() - paisSave1.getMortes();
            long recuperadosDiferenca = paisSave2.getRecuperados() - paisSave1.getRecuperados();


            //Usar os historicos agora persistidos no banco para esta edição
            ben.setNomeHistorico(edit.nomeBench());
            ben.setHistoricoPais1(paisSave1);
            ben.setHistoricoPais2(paisSave2);
            ben.setDataHistorico(sdf.format(date));
            ben.setDataIncial(dataInicial);
            ben.setDataFinal(dataFinal);
            ben.setConfirmadosDiferenca(confirmadosDiferenca < 0 ? confirmadosDiferenca *= -1 : confirmadosDiferenca);
            ben.setMortesDiferenca(mortesDiferenca < 0 ? mortesDiferenca *= -1 : mortesDiferenca);
            ben.setRecuperadosDiferenca(recuperadosDiferenca < 0 ? recuperadosDiferenca *= -1 : recuperadosDiferenca);

            LOG.info("Benchmark editado. Vou tentar persistir o mesmo.");
            Benchmark benchm = bench.save(ben);

            DadosRespEdicaoBenchmark dadosResposta = CriarRecords.obterRespostaEdicaoBenchmark(benchm);
            LOG.info("Benchmark de nome " + dadosResposta.nomeBench() + " editado e salvo.");
            return ResponseEntity.ok(dadosResposta);

        } catch (Exception e) {
            LOG.error("Houve um erro ao tentar editar e persistir o benchmark.", e);
            DadosRespEdicaoBenchmark dados = new DadosRespEdicaoBenchmark(id, nomeBench, false,"Houve um erro ao tentar editar e persistir o benchmark. "+ e.getMessage());
            return ResponseEntity.badRequest().body(dados);
        }
    }

    /**
     * VERIFICAÇÕES E UTILS DA CLASSE
     */

    public DadosRespostaReportPais obterHistoricoPelaSiglaDatas(String sigla1, String dataInicial, String dataFinal) throws Exception{
        String url = Requisicao.REPORT_TOTAL_DIA_PAIS_POR_DATA_SIGLA.get();
        String jsonPaisResp1 = null;
        String jsonPaisResp2 = null;
        Pais paisAux1 = obterPaisSeExisteOuNao(sigla1);
        DadosRespostaReportPais dadosPais1 = null;

        if(seExisteHistoricoSalvoParaPais(paisAux1, dataInicial, dataFinal)){
            LOG.info("Não existe histórico para país de sigla " + sigla1 + " e datas " + dataInicial + " a " + dataFinal + ". Vou realizar request para obter o histórico.");
            String urlWDataInicial = url.replace("{DATA}", dataInicial).replace("{SIGLA}", sigla1);
            String urlWDataFinal = url.replace("{DATA}", dataFinal).replace("{SIGLA}", sigla1);
            jsonPaisResp1 = Requests.realizarRequest(urlWDataInicial);
            jsonPaisResp2 = Requests.realizarRequest(urlWDataFinal);
            DadosRespostaReportPais dados1 = CriarRecords.montarRecordRespostaDadosPais(paisAux1, jsonPaisResp1, dataFinal);
            DadosRespostaReportPais dados2 = CriarRecords.montarRecordRespostaDadosPais(paisAux1, jsonPaisResp2, dataFinal);
            long confirm = dados2.confirmados() - dados1.confirmados();
            long mortes = dados2.mortes() - dados1.mortes();
            long recuperados = dados2.recuperados() - dados1.recuperados();
            dadosPais1 = new DadosRespostaReportPais(
                sigla1,
                paisAux1.getNome(),
                dataInicial,
                dataFinal,
                dados1.ultUpdate(),
                confirm < 0 ? confirm *= (-1) : confirm,
                mortes < 0 ? mortes *= (-1) : mortes,
                recuperados < 0 ? recuperados *= (-1) : recuperados,
                dados1.taxaFatalidade()
            );
            return dadosPais1;
        } else {
            LOG.info("Já existe histórico para país de sigla " + sigla1 + " e datas " + dataInicial + " a " + dataFinal + ". Vou buscar no banco e usar neste Benchmark.");
            var historicoFound = historico.findHistoricoPais(paisAux1, dataInicial, dataFinal);
            return CriarRecords.montarDadosRespostaReportByHistoricoPais(historicoFound.get());
        }
    }


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
            if (!Datas.isCorreto(pais1.dataInicial()) || !Datas.isCorreto(pais1.dataFinal())) {
                throw new Exception("Não vou persistir esse Benchmark porque o país de " + siglaPais1 + " não tem informações de data, provavelmente não tem informações sobre esse país");
            }

            if (!Datas.isCorreto(pais2.dataInicial()) || !Datas.isCorreto(pais2.dataFinal())) {
                throw new Exception("Não vou persistir esse Benchmark porque o país de " + siglaPais1 + " não tem informações de data, provavelmente não tem informações sobre esse país");
            }

            //Salvar os dois historicos pais
            HistoricoPais paisSave1;
            HistoricoPais paisSave2;
            if (seExisteHistoricoSalvoParaPais(paisAux1, pais1.dataInicial(), pais1.dataFinal())) {
                LOG.info("Não existe um histórico para o país " + siglaPais1 + " , então vou salvar o mesmo no banco local.");
                paisSave1 = historico.save(new HistoricoPais(pais1, paisAux1));
                LOG.info("Historico do país " + siglaPais1 + " salvo com sucesso e disponível para este Benchmark");
            } else {
                LOG.info("Já existe um histórico para o país " + siglaPais1 + " de mesma data, logo vou usar o mesmo.");
                paisSave1 = historico.findHistoricoPais(paisAux1, pais1.dataInicial(), pais1.dataFinal()).get();
            }

            if (seExisteHistoricoSalvoParaPais(paisAux2, pais2.dataInicial(), pais2.dataFinal())) {
                LOG.info("Não existe um histórico para o país " + siglaPais2 + " , então vou salvar o mesmo no banco local.");
                paisSave2 = historico.save(new HistoricoPais(pais2, paisAux2));
                LOG.info("Historico do país " + siglaPais2 + " salvo com sucesso e disponível para este Benchmark");
            } else {
                LOG.info("Já existe um histórico para o país " + siglaPais2 + " de mesma data, logo vou usar o mesmo.");
                paisSave2 = historico.findHistoricoPais(paisAux2, pais2.dataInicial(), pais2.dataFinal()).get();
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
                    benchmark.confirmadosTotal(),
                    benchmark.mortesTotal(),
                    benchmark.recuperadosTotal(),
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
            var hpAux = historico.findHistoricoPais(pais, dataInicial, dataFinal);
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
