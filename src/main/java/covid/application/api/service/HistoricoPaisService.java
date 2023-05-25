package covid.application.api.service;

import covid.application.api.external.Requests;
import covid.application.api.util.CriarRecords;
import covid.application.api.modelos.entidade.*;
import covid.application.api.modelos.enums.Requisicao;
import covid.application.api.modelos.records.*;
import covid.application.api.repository.*;
import covid.application.api.threads.*;
import covid.application.api.util.Datas;
import covid.application.api.util.Verificar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HistoricoPaisService {

    @Autowired
    private HistoricoPaisRepository histPais;

    @Autowired
    private PaisRepository pais;

    private static Logger LOG = LoggerFactory.getLogger(HistoricoPaisService.class);

    Verificar verifica = new Verificar();

    public ResponseEntity obterDadosByData(DadosReportBuscaDataSigla dados) throws Exception {

        String sigla = dados.sigla().toUpperCase();

        if (!seSiglaTamanhoCorreto(sigla)) {
            return ResponseEntity.badRequest().body("A Sigla " + sigla + " está incorreta. Por favor verifique-a.");
        }

        //Verifica se sigla existe no banco, se não então é provável que não tenha nenhum historico de pesquisa já salvo
        Pais paisEncontrado = new Pais();

        LOG.info("Verificacao se país com  sigla " + sigla + " existe localmente");
        boolean achouSigla = false;
        if (sePaisExisteSalvoLocalPelaSigla(sigla)) {
            paisEncontrado = pais.findBySigla(sigla).get();
        } else {
            LOG.info("Não encontrei o pais pela Sigla " + sigla + ", vou iniciar uma busca na API externa");
            String url = Requests.realizarRequest(Requisicao.OBTER_TODOS_PAISES_E_SIGLAS.get());
            List<DadosPaisesSigla> listaDados = CriarRecords.montarRecordPaisesSigla(url);

            LOG.info("Irei verificar se a SIGLA " + sigla + " existe na resposta da API");
            for (DadosPaisesSigla dadosP : listaDados) {
                if (dadosP.sigla().equalsIgnoreCase(sigla) || dadosP.sigla().contains(sigla)) {
                    LOG.info("Pais encontrado pela Sigla " + sigla + " na API Externa! " + dados + ". O mesmo será persistido no banco local");
                    paisEncontrado = pais.save(new Pais(dadosP));
                    achouSigla = true;
                }
            }
            LOG.info("País de sigla " + sigla +" salvo com sucesso");
        }

        if (!achouSigla){
            return ResponseEntity.badRequest().body("Sigla " + sigla + " não existe para API externa, tente outra sigla");
        }

        //Verifica já existe algum histórico no banco, se sim, então retorna essa info
        boolean verificacaoHistorico = historicoPaisSeExiste(paisEncontrado, dados.data());
        if (verificacaoHistorico) {
            try {
                HistoricoPais historicoP = histPais.findHistoricoPaisByDataUnica(paisEncontrado, dados.data()).get();
                DadosRespostaReportPais dadosResposta = CriarRecords.montarDadosRespostaReportByHistoricoPais(historicoP);
                LOG.info("Historico de País sigla " + sigla + " e data " + dados.data() + " já existe no banco local, logo, vou enviar ao cliente.");
                return ResponseEntity.ok(dadosResposta);
            } catch (Exception e) {
                LOG.error("Houve um erro ao validar se existe o histórico no banco.", e);
            }
        }

        //Se chegou nessa parte, significa que não tem histórico para o país, logo, dar sequencia para obter um novo histórico

        //Preparar URL para requisitar da API externa
        String urlCrua = Requisicao.REPORT_TOTAL_DIA_PAIS_POR_DATA_SIGLA.get();

        String urlPronta = urlCrua.replace("{DATA}", dados.data()).replace("{SIGLA}", sigla);

        //Obter JSON resposta da API externa
        String jsonResultado = Requests.realizarRequest(urlPronta);

        DadosRespostaReportPais dadosObtidos = CriarRecords.montarRecordRespostaDadosPais(sigla, jsonResultado);
        try {
            histPais.save(new HistoricoPais(dadosObtidos, paisEncontrado));
            LOG.info("Salvo histórico do pais " + paisEncontrado.getNome());
        } catch (Exception e) {
            LOG.error("Não foi possível persistir Historico no banco.", e);
        }

        LOG.info("Report Pais enviado ao Cliente com sucesso.");
        return ResponseEntity.ok(dadosObtidos);
    }

    public ResponseEntity obterDadosPaisPorFaixaDatas(DadosBuscaPaisDatas dadosBusca) throws Exception {

        String sigla = dadosBusca.sigla().toUpperCase();
        String dataInicial = dadosBusca.dataInicial();
        String dataFinal = dadosBusca.dataFinal();

        if (sigla.length() != 3) {
            LOG.error("A Sigla " + sigla + " está incorreta. Siglas devem conter 3 caracteres.");
            return ResponseEntity.badRequest().body("A Sigla " + sigla + " está incorreta. Siglas devem conter 3 caracteres.");
        }

        if (!Datas.isSequencial(dataInicial, dataFinal)) {
            LOG.error("A data " + dadosBusca.dataInicial() + " deve ser anterior a data " + dadosBusca.dataFinal());
            return ResponseEntity.badRequest().body("A data " + dadosBusca.dataInicial() + " deve ser anterior a data " + dadosBusca.dataFinal());
        }

        LOG.info("Irei verificar se pais de sigla "+sigla+ " existe no banco local. Se não buscarei na API externa e persistirei localmente.");
        if(!sePaisExisteSalvoLocalPelaSigla(sigla)){

        } else {
            LOG.info("Pais de sigla "+sigla+" encontrado, vou continuar com a solicitação de dados para este país");
        }

        LOG.info("irei verificar se já existe algum histórico do país de Sigla " + sigla + " para as datas " + dadosBusca.dataInicial() + " até " + dadosBusca.dataFinal());

        if (historicoPaisSeExiste(dadosBusca)) {
            LOG.info("Encontrado um histórico com mesma data no banco local. Logo, vou enviar o mesmo ao cliente.");
            Pais paisAux = pais.findBySigla(dadosBusca.sigla()).get();
            HistoricoPais historico = histPais.findHistoricoPais(paisAux, dadosBusca.dataInicial(), dadosBusca.dataFinal()).get();
            DadosRespostaReportPais resposta = CriarRecords.montarDadosRespostaReportByHistoricoPais(historico);
            return ResponseEntity.ok().body(resposta);
        }

        LOG.info("Inicio para obter informacoes de País de sigla " + sigla + " por datas " + dadosBusca.dataInicial() + " até " + dadosBusca.dataFinal());
        List<RequestThread> listaRun = new ArrayList<>();

        long confirmados = 0;
        long mortes = 0;
        long recuperados = 0;


        String url = Requisicao.REPORT_TOTAL_DIA_PAIS_POR_DATA_SIGLA.get();

        String url1 = url.replace("{DATA}", dadosBusca.dataInicial()).replace("{SIGLA}", sigla);
        String url2 = url.replace("{DATA}", dadosBusca.dataFinal()).replace("{SIGLA}", sigla);

        listaRun.add(new RequestThread(url1));
        listaRun.add(new RequestThread(url2));

        LOG.info("Preparo para disparo de requisicoes a API externa para obter a informação completa. " + listaRun.size());

        LOG.info("Iniciando requisicoes a API externa...");
        for (RequestThread rt : listaRun) {
            rt.run();
        }

        List<String> results = RequestThreadRepository.getLista();
        LOG.info("A requisicao obteve " + results.size() + " resultados. Vamos compilar em apenas 1 para o cliente");

        DadosRespostaReportPais dadosPais1 = CriarRecords.montarRecordRespostaDadosPais(sigla, results.get(0), dataFinal);
        DadosRespostaReportPais dadosPais2 = CriarRecords.montarRecordRespostaDadosPais(sigla, results.get(1), dataFinal);

        RequestThreadRepository.limparLista();

        //novo calculo da diferenca entre a primeira data com a segunda data
        confirmados = dadosPais2.confirmados() - dadosPais1.confirmados();
        mortes = dadosPais2.mortes() - dadosPais1.mortes();
        recuperados = dadosPais2.recuperados() - dadosPais1.recuperados();

        //Montar os dados no record DadosRespostaReportPais para retorno ao Cliente
        DadosRespostaReportPais dadosTotal = new DadosRespostaReportPais(
                sigla,
                dataInicial,
                dadosPais2.dataFinal(),
                dadosPais2.ultUpdate(),
                confirmados,
                mortes,
                recuperados,
                (mortes / (double) confirmados) * 100
        );

        LOG.info("Dados a serem enviados ao Cliente: " + dadosTotal);
        try {
            LOG.info("Iremos persistir esse dados no Historico de Pais");
            saveHistPais(dadosBusca, dadosTotal);
        } catch (Exception e) {
            LOG.error("Erro ao persistir Historico Pais.", e);
        }

        return ResponseEntity.ok(dadosTotal);
    }

    private void saveHistPais(DadosBuscaPaisDatas dataBuscas, DadosRespostaReportPais dadosTotal) {
        Pais paisAux = pais.findBySigla(dadosTotal.sigla()).get();
        histPais.save(new HistoricoPais(dataBuscas, dadosTotal, paisAux));
        LOG.info("Salvo histórico do pais " + paisAux.getNome());
    }

    public boolean historicoPaisSeExiste(DadosBuscaPaisDatas dadosBusca) {
        String sigla = dadosBusca.sigla();
        try {
            var paisAux = pais.findBySigla(sigla);
            Pais pais = paisAux.get();
            var hist = histPais.findHistoricoPais(pais, dadosBusca.dataInicial(), dadosBusca.dataFinal());
            return !hist.isEmpty();
        } catch (Exception e) {
            LOG.error("Não foi possível realiza a verificacao para o país de sigla " + sigla, e);
            return false;
        }
    }

    public boolean historicoPaisSeExiste(Pais pais, String data) {
        try {
            var hist = histPais.findHistoricoPaisByDataUnica(pais, data);
            if (hist.isEmpty()) {
                LOG.info("Não existe o histórico no banco para país de nome " + pais.getNome() +
                        ", sigla " + pais.getSigla() +
                        " e data " + data);
                return false;
            }
            return true;
        } catch (Exception e) {
            LOG.error("Não foi possível realizar a verificacao de histórico do país de sigla " + pais.getSigla() + " e data " + data, e);
            return false;
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

}
