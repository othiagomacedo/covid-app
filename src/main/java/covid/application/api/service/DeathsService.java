package covid.application.api.service;

import covid.application.api.modelos.records.DadosBuscaLocalidade;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DeathsService {

    public static ResponseEntity obterDeathByData(DadosBuscaLocalidade dados){

        return ResponseEntity.ok().build();
    }

    public static ResponseEntity obterDeathEstadoByData(DadosBuscaLocalidade dados){
        if (dados.nomeLocalidade().trim().length() == 2){

        }
        return ResponseEntity.ok().build();
    }

    public static ResponseEntity obterDeathPaisByData(DadosBuscaLocalidade dados){

        return ResponseEntity.ok().build();
    }
}
