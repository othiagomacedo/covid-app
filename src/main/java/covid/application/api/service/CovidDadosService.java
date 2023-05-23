package covid.application.api.service;

import covid.application.api.external.Requests;
import covid.application.api.external.RequisicaoPorPais;
import covid.application.api.modelos.entidade.HistoricoPais;
import covid.application.api.modelos.entidade.Pais;
import covid.application.api.modelos.enums.Requisicao;
import covid.application.api.modelos.records.DadosBuscaPaisDatas;
import covid.application.api.modelos.records.DadosPaisesSigla;
import covid.application.api.modelos.records.DadosReportBuscaDataSigla;
import covid.application.api.modelos.records.DadosRespostaReportPais;
import covid.application.api.repository.HistoricoBenchmarkRepository;
import covid.application.api.repository.HistoricoPaisRepository;
import covid.application.api.repository.PaisRepository;
import covid.application.api.threads.RequestThread;
import covid.application.api.threads.RequestThreadRepository;
import covid.application.api.util.Datas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CovidDadosService {

    @Autowired
    private HistoricoPaisRepository histPais;

    @Autowired
    private HistoricoBenchmarkRepository bench;

    @Autowired
    private PaisRepository pais;

    private static Logger LOG = LoggerFactory.getLogger(CovidDadosService.class);


    public ResponseEntity obterDadosByData(DadosReportBuscaDataSigla dados) throws Exception {

        //Verifica se sigla existe no banco, se não então é certo que não tenha nenhum historico de pesquisa já salvo
        String sigla = dados.sigla().toUpperCase();

        LOG.info("Verificacao se país com  sigla "+sigla+ " existe localmente");
        var paisE = pais.findBySigla(sigla);

        if (paisE.isEmpty()){
            LOG.info("Não encontrei o pais pela Sigla "+dados.sigla().toUpperCase()+", vou iniciar uma busca na API externa");
            String retorno = Requests.realizarRequest(Requisicao.OBTER_TODOS_PAISES_E_SIGLAS.get());
            List<DadosPaisesSigla> listaDados = RequisicaoPorPais.montarRecordPaisesSigla(retorno);

            for (DadosPaisesSigla dadosP : listaDados){
                if (dadosP.sigla().equalsIgnoreCase(sigla) || dadosP.sigla().contains(sigla)){
                    LOG.info("Pais encontrado pela Sigla "+sigla.toUpperCase()+" na API Externa! "+dados+". O mesmo será persistido no banco local");
                    pais.save(new Pais(dadosP));
                }
            }
        }

        return ResponseEntity.ok(dados);
    }

    public ResponseEntity obterDadosPaisPorFaixaDatas(DadosBuscaPaisDatas dadosBusca) throws Exception{
        if (Datas.isSequencial(dadosBusca.dataInicial(),dadosBusca.dataFinal())){
            LOG.info("Inicio para obter informacoes de País de sigla "+dadosBusca.sigla()+" por datas "+dadosBusca.dataInicial()+" até "+dadosBusca.dataFinal());
            List<RequestThread> listaRun = new ArrayList<>();
            List<DadosRespostaReportPais> listaDados = new ArrayList<>();
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            long confirmados = 0;
            long mortes = 0;
            long recuperados = 0;


            String url = Requisicao.REPORT_TOTAL_DIA_PAIS_POR_DATA_SIGLA.get();

            String url1 = url.replace("{DATA}", dadosBusca.dataInicial()).replace("{SIGLA}", dadosBusca.sigla());
            String url2 = url.replace("{DATA}", dadosBusca.dataFinal()).replace("{SIGLA}", dadosBusca.sigla());

            listaRun.add(new RequestThread(url1));
            listaRun.add(new RequestThread(url2));

            LOG.info("Preparo para disparo de requisicoes a API externa para obter a informação completa. "+listaRun.size());

            LOG.info("Iniciando requisicoes a API externa...");
            for (RequestThread rt : listaRun){
                rt.run();
            }

            List<String> results = RequestThreadRepository.getLista();
            LOG.info("A requisicao obteve "+results.size()+" resultados. Vamos compilar em apenas 1 para o cliente");

            DadosRespostaReportPais dadosPais1 = RequisicaoPorPais.montarRecordRespostaDadosPais(results.get(0));
            DadosRespostaReportPais dadosPais2 = RequisicaoPorPais.montarRecordRespostaDadosPais(results.get(1));

            //novo calculo da diferenca entre a primeira data com a segunda data
            confirmados = dadosPais2.confirmados() - dadosPais1.confirmados();
            mortes = dadosPais2.mortes() - dadosPais1.mortes();
            recuperados = dadosPais2.recuperados() - dadosPais1.recuperados();

            //Montar os dados no record DadosRespostaReportPais para retorno ao Cliente
            DadosRespostaReportPais dadosTotal = new DadosRespostaReportPais(
                    dadosBusca.sigla(),
                    sdf.format(date),
                    sdf.format(date),
                    confirmados,
                    mortes,
                    recuperados,
                    (mortes/(double)confirmados) * 100
            );

            LOG.info("Dados a serem enviados ao Cliente: "+dadosTotal);
            LOG.info("Iremos persistir esse dados no Historico de Pais");
            saveHistPais(dadosBusca,dadosTotal);

            return ResponseEntity.ok(dadosTotal);

        } else {
            LOG.error("A data "+dadosBusca.dataInicial()+ " deve ser anterior a data "+dadosBusca.dataFinal());
            return ResponseEntity.badRequest().body("A data "+dadosBusca.dataInicial()+ " deve ser anterior a data "+dadosBusca.dataFinal());
        }
    }

    private void saveHistPais(DadosBuscaPaisDatas dataBuscas, DadosRespostaReportPais dadosTotal) throws Exception {
        Pais paisAux = pais.findBySigla(dadosTotal.sigla()).get();
        histPais.save(new HistoricoPais(dataBuscas,dadosTotal, paisAux));
        LOG.info("Salvo histórico do pais "+paisAux.getNome());
    }
}
