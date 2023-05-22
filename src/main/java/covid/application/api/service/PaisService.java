package covid.application.api.service;

import covid.application.api.external.Requests;
import covid.application.api.external.RequisicaoPorPais;
import covid.application.api.modelos.entidade.Pais;
import covid.application.api.modelos.enums.Requisicao;
import covid.application.api.modelos.records.DadosPaisesSigla;
import covid.application.api.modelos.records.DadosReportBuscaDataSigla;
import covid.application.api.modelos.records.ListaDadosPaisesSigla;
import covid.application.api.repository.PaisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaisService {

    @Autowired
    private PaisRepository pais;

    private static Logger LOG = LoggerFactory.getLogger(PaisService.class);


    public ResponseEntity obterPaisByNome(String nome) throws Exception {
        var paisE = pais.findByNome(nome);

        if (paisE.isEmpty()){
            LOG.debug("Não encontrei o pais pelo nome "+nome.toUpperCase()+", vou iniciar uma busca na API externa");
            String retorno = Requests.realizarRequest(Requisicao.OBTER_TODOS_PAISES_E_SIGLAS.get());

        }

        Pais p = paisE.get();
        DadosPaisesSigla dados = new DadosPaisesSigla(p.getNome(), p.getSigla());
        return ResponseEntity.ok(dados);
    }

    public ResponseEntity obterPaisBySigla(String sigla){
        var paisE = pais.findBySigla(sigla);
        Pais p = paisE.get();
        DadosPaisesSigla dados = new DadosPaisesSigla(p.getNome(), p.getSigla());
        return ResponseEntity.ok(dados);
    }
}
