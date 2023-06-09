package covid.application.api.service;

import covid.application.api.external.Requests;
import covid.application.api.util.CriarRecords;
import covid.application.api.modelos.entidade.Pais;
import covid.application.api.modelos.enums.Requisicao;
import covid.application.api.modelos.records.DadosPaisesSigla;
import covid.application.api.repository.PaisRepository;
import covid.application.api.util.Verificar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class PaisService {

    @Autowired
    private PaisRepository pais;

    Verificar verifica;

    public PaisService() {
        verifica  = new Verificar();
    }

    private static Logger LOG = LoggerFactory.getLogger(PaisService.class);

    public ResponseEntity obterPaisByNome(String nome) throws Exception {
        var paisE = pais.findByNome(nome.toUpperCase());
        DadosPaisesSigla dados;

        if (paisE.isEmpty()){
            boolean achou = false;

            LOG.info("Não encontrei o pais pelo nome "+nome.toUpperCase()+", vou iniciar uma busca na API externa");
            String retorno = Requests.realizarRequest(Requisicao.OBTER_TODOS_PAISES_E_SIGLAS.get());
            List<DadosPaisesSigla> listaDados = CriarRecords.montarRecordPaisesSigla(retorno);

            for (DadosPaisesSigla dadosP : listaDados){
                if (dadosP.nome().equalsIgnoreCase(nome) || dadosP.nome().contains(nome)){
                    achou = true;
                    dados = dadosP;
                    LOG.info("Pais encontrado na API Externa! "+dados+". O mesmo será persistido no banco local e enviado ao Cliente");
                    pais.save(new Pais(dadosP));
                    return ResponseEntity.ok(dados);
                }
            }

            if (!achou){
                return ResponseEntity.badRequest().body("Pais com nome "+nome.toUpperCase()+" não encontrado");
            }

        } else {
            Pais p = paisE.get();
            dados = new DadosPaisesSigla(p.getNome(), p.getSigla());
            LOG.info("Pais encontrado: "+dados+". Será enviado ao Cliente.");
            return ResponseEntity.ok(dados);
        }

        return ResponseEntity.notFound().build();
    }

    public ResponseEntity obterPaisBySigla(String sigla) throws Exception{
        try{
            //Verifica se sigla existe no banco, se não então é certo que não tenha nenhum historico de pesquisa já salvo
            if (!seSiglaTamanhoCorreto(sigla)){
                LOG.error("A Sigla "+sigla+" está incorreta.");
                return ResponseEntity.badRequest().body("A Sigla "+sigla+" está incorreta. Por favor verifique-a");
            }

            return ResponseEntity.ok(obterPaisBySiglas(sigla));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Pais com sigla "+sigla + " não encontrado.");
        }
    }

    public DadosPaisesSigla obterPaisBySiglas(String sigla)throws Exception{
        var paisE = pais.findBySigla(sigla.toUpperCase());
        DadosPaisesSigla dados;

        if (paisE.isEmpty()){
            LOG.info("Não encontrei o pais pela Sigla "+sigla.toUpperCase()+", vou iniciar uma busca na API externa");
            String retorno = Requests.realizarRequest(Requisicao.OBTER_TODOS_PAISES_E_SIGLAS.get());
            List<DadosPaisesSigla> listaDados = CriarRecords.montarRecordPaisesSigla(retorno);

            for (DadosPaisesSigla dadosP : listaDados){
                if (dadosP.sigla().equalsIgnoreCase(sigla) || dadosP.sigla().contains(sigla)) {
                    dados = dadosP;
                    LOG.info("Pais encontrado pela Sigla " + sigla.toUpperCase() + " na API Externa! " + dados + ". O mesmo será persistido no banco local e enviado ao Cliente");
                    pais.save(new Pais(dados));
                    return dadosP;
                }
            }

        }

        Pais p = paisE.get();
        dados = new DadosPaisesSigla(p.getNome(), p.getSigla());
        LOG.info("Pais encontrado pela sigla: "+dados+". Será enviado ao Cliente.");
        return dados;
    }

    public ResponseEntity getTodosPaisesCadastrados() throws Exception {
        LOG.info("Obtendo a lista de paises (nome e sigla) persistidos atualmente");
        List<Pais> lista = pais.findAll();
        LOG.info("Quantidade da Lista de paises: "+lista.size()+". Será enviada lista ao Cliente.");
        return ResponseEntity.ok(lista);
    }

    public void obterTodosPaisesAPIExterna(){
        try{
            boolean hasPais = false;
            try{
                List<Pais> lista = pais.findAll();
                if (lista.size() > 0) {
                    hasPais = true;
                }
            } catch (Exception e) {
                LOG.warn("Nao foi possível obter a lista de países persistidos no banco local.");
            }
            if (!hasPais) {
                String url = Requests.realizarRequest(Requisicao.OBTER_TODOS_PAISES_E_SIGLAS.get());
                List<DadosPaisesSigla> listaDados = CriarRecords.montarRecordPaisesSigla(url);
                List<Pais> listaPais = new ArrayList<>();
                for (DadosPaisesSigla pais : listaDados) {
                    listaPais.add(new Pais(pais));
                }
                pais.saveAll(listaPais);
                LOG.info("Obtido uma lista de "+listaPais.size()+" e todos salvos localmente para consultas futuras");
            }
        } catch (Exception e) {
            LOG.error("Nao foi possível persistir a lista de países da API externa", e);
        }
    }

    public boolean seSiglaTamanhoCorreto(String sigla) {
        if (sigla.length() != 3) {
            LOG.error("A Sigla " + sigla + " está incorreta. Siglas devem conter 3 caracteres.");
            return false;
        }
        return true;
    }
}
