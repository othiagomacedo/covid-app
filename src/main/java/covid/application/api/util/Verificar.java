package covid.application.api.util;

import covid.application.api.modelos.entidade.Benchmark;
import covid.application.api.modelos.entidade.HistoricoPais;
import covid.application.api.modelos.entidade.Pais;
import covid.application.api.modelos.records.DadosBuscaBenchmark;
import covid.application.api.modelos.records.DadosBuscaPaisDatas;
import covid.application.api.repository.BenchmarkRepository;
import covid.application.api.repository.HistoricoPaisRepository;
import covid.application.api.repository.PaisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Verificar {

    @Autowired
    private BenchmarkRepository bench;

    @Autowired
    private HistoricoPaisRepository histPais;

    @Autowired
    private PaisRepository paisRepo;

    @Autowired
    public Verificar(){}

    private static Logger LOG = LoggerFactory.getLogger(Verificar.class);

    public boolean historicoPaisSeExiste(DadosBuscaPaisDatas dadosBusca) {
        String sigla = dadosBusca.sigla();
        try {
            var paisAux = paisRepo.findBySigla(sigla);
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
            var paisBuscado = paisRepo.findBySigla(sigla);
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

    public boolean seExisteBenchmark(DadosBuscaBenchmark dados){
        try{
            String dataInicial = dados.dataInicial();
            String dataFinal = dados.dataFinal();
            Pais pais1 = paisRepo.findBySigla(dados.paisSigla1()).get();
            Pais pais2 = paisRepo.findBySigla(dados.paisSigla2()).get();
            HistoricoPais historicoPais1 = histPais.findHistoricoPais(pais1,dataInicial,dataFinal).get();
            HistoricoPais historicoPais2 = histPais.findHistoricoPais(pais2,dataInicial,dataFinal).get();

            Benchmark benchs = bench.findHistoricoBenchmark(historicoPais1,historicoPais2,dataInicial,dataFinal).get();
            return benchs == null;
        }catch (Exception e){
            LOG.error("Houve um erro ao tentar validar se historico benchmark existe. ",e);
            return false;
        }
    }

    public boolean sePaisExisteSalvo(Pais pais) {
        String sigla = pais.getSigla();
        try {
            var paisBuscado = paisRepo.findBySigla(sigla);
            if (!paisBuscado.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            LOG.warn("Não foi possível buscar país com sigla " + sigla, e);
        }
        return false;
    }


}
