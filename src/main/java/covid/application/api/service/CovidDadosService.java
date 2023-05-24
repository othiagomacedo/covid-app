package covid.application.api.service;

import covid.application.api.external.Requests;
import covid.application.api.util.CriarRecords;
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

        if (sigla.length() != 3){
            LOG.error("A Sigla "+sigla+" está incorreta. Siglas devem conter 3 caracteres.");
            return ResponseEntity.badRequest().body("A Sigla "+sigla+" está incorreta. Siglas devem conter 3 caracteres.");
        }

        LOG.info("Verificacao se país com  sigla "+sigla+ " existe localmente");
        var paisE = pais.findBySigla(sigla);

        //Verifica já existe alguma pesquisa no banco, se sim, então retorna essa info
        if(verificaHistoricoPaisSeExiste(paisE.get(),dados.data())){
            try{
                var histAux = histPais.findHistoricoPaisByDataUnica(paisE.get(),dados.data());
                if (histAux.isEmpty()){
                    LOG.info("Não existe o histórico no banco, logo, vou continuar com a pesquina na API externa.");
                } else {
                    HistoricoPais historicoP = histAux.get();
                    DadosRespostaReportPais dadosResposta = CriarRecords.montarDadosRespostaReportByHistoricoPais(historicoP);
                    LOG.info("Historico de País sigla "+sigla+" e data "+dados.data()+" já existe no banco local, logo, vou enviar ao cliente.");
                    return ResponseEntity.ok(dadosResposta);
                }
            } catch (Exception e){
                LOG.error("Houve um erro ao validar se existe o histórico no banco.",e);
            }
        }

        Pais paisAux = new Pais();

        if (paisE.isEmpty()){
            LOG.info("Não encontrei o pais pela Sigla "+sigla+", vou iniciar uma busca na API externa");
            String url = Requests.realizarRequest(Requisicao.OBTER_TODOS_PAISES_E_SIGLAS.get());
            List<DadosPaisesSigla> listaDados = CriarRecords.montarRecordPaisesSigla(url);

            LOG.info("Irei verificar se a SIGLA "+sigla+" existe na resposta da API");
            for (DadosPaisesSigla dadosP : listaDados){
                if (dadosP.sigla().equalsIgnoreCase(sigla) || dadosP.sigla().contains(sigla)){
                    LOG.info("Pais encontrado pela Sigla "+sigla+" na API Externa! "+dados+". O mesmo será persistido no banco local");
                    paisAux = new Pais(dadosP);
                    pais.save(paisAux);
                }
            }
        } else {
            paisAux = paisE.get();
        }

        //Preparar URL para requisitar da API externa
        String urlCrua = Requisicao.REPORT_TOTAL_DIA_PAIS_POR_DATA_SIGLA.get();

        String urlPronta = urlCrua.replace("{DATA}", dados.data()).replace("{SIGLA}", sigla);

        //Obter JSON resposta da API externa
        String jsonResultado = Requests.realizarRequest(urlPronta);

        DadosRespostaReportPais dadosObtidos = CriarRecords.montarRecordRespostaDadosPais(sigla,jsonResultado);
        try{
            histPais.save(new HistoricoPais(dadosObtidos, paisAux));
            LOG.info("Salvo histórico do pais "+paisAux.getNome());
        }catch (Exception e){
            LOG.error("Não foi possível persistir Historico no banco.",e);
        }

        LOG.info("Report Pais enviado ao Cliente com sucesso.");
        return ResponseEntity.ok(dadosObtidos);
    }

    public ResponseEntity obterDadosPaisPorFaixaDatas(DadosBuscaPaisDatas dadosBusca) throws Exception{

        //Verifica se sigla existe no banco, se não então é certo que não tenha nenhum historico de pesquisa já salvo
        String sigla = dadosBusca.sigla().toUpperCase();

        if (sigla.length() != 3){
            LOG.error("A Sigla "+sigla+" está incorreta. Siglas devem conter 3 caracteres.");
            return ResponseEntity.badRequest().body("A Sigla "+sigla+" está incorreta. Siglas devem conter 3 caracteres.");
        }

        if (!Datas.isSequencial(dadosBusca.dataInicial(),dadosBusca.dataFinal())){
            LOG.error("A data "+dadosBusca.dataInicial()+ " deve ser anterior a data "+dadosBusca.dataFinal());
            return ResponseEntity.badRequest().body("A data "+dadosBusca.dataInicial()+ " deve ser anterior a data "+dadosBusca.dataFinal());
        }


        LOG.info("Inicio para obter informacoes de País de sigla "+dadosBusca.sigla()+" por datas "+dadosBusca.dataInicial()+" até "+dadosBusca.dataFinal());
        List<RequestThread> listaRun = new ArrayList<>();

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

        DadosRespostaReportPais dadosPais1 = CriarRecords.montarRecordRespostaDadosPais(dadosBusca.sigla(), results.get(0));
        DadosRespostaReportPais dadosPais2 = CriarRecords.montarRecordRespostaDadosPais(dadosBusca.sigla(), results.get(1));

        RequestThreadRepository.limparLista();

        //novo calculo da diferenca entre a primeira data com a segunda data
        confirmados = dadosPais2.confirmados() - dadosPais1.confirmados();
        mortes = dadosPais2.mortes() - dadosPais1.mortes();
        recuperados = dadosPais2.recuperados() - dadosPais1.recuperados();

        //Montar os dados no record DadosRespostaReportPais para retorno ao Cliente
        DadosRespostaReportPais dadosTotal = new DadosRespostaReportPais(
                dadosBusca.sigla(),
                dadosBusca.dataInicial(),
                dadosPais2.dataFinal(),
                dadosPais2.ultUpdate(),
                confirmados,
                mortes,
                recuperados,
                (mortes/(double)confirmados) * 100
        );

        LOG.info("Dados a serem enviados ao Cliente: "+dadosTotal);
        try{
            LOG.info("Iremos persistir esse dados no Historico de Pais");
            saveHistPais(dadosBusca,dadosTotal);
        }catch (Exception e){
            LOG.error("Erro ao persistir Historico Pais.",e);
        }

        return ResponseEntity.ok(dadosTotal);
    }

    private void saveHistPais(DadosBuscaPaisDatas dataBuscas, DadosRespostaReportPais dadosTotal){
        Pais paisAux = pais.findBySigla(dadosTotal.sigla()).get();
        histPais.save(new HistoricoPais(dataBuscas,dadosTotal, paisAux));
        LOG.info("Salvo histórico do pais "+paisAux.getNome());
    }

    private boolean verificaHistoricoPaisSeExiste(Pais pais,String dataInicial, String dataFinal){
        var hist = histPais.findHistoricoPais(pais,dataInicial,dataFinal);
        return hist.isEmpty();
    }

    private boolean verificaHistoricoPaisSeExiste(Pais pais,String data){
        var hist = histPais.findHistoricoPaisByDataUnica(pais,data);
        return !hist.isEmpty();
    }
}
