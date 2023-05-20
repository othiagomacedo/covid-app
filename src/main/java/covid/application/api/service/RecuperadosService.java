package covid.application.api.service;

import covid.application.api.records.DadosBuscaLocalidade;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RecuperadosService {

    public static ResponseEntity obterRecupCidadeByData(DadosBuscaLocalidade dados){

        return ResponseEntity.ok().build();
    }

    public static ResponseEntity obterRecupEstadoByData(DadosBuscaLocalidade dados){
        if (dados.nomeLocalidade().trim().length() == 2){

        }
        return ResponseEntity.ok().build();
    }

    public static ResponseEntity obterRecupPaisByData(DadosBuscaLocalidade dados){

        return ResponseEntity.ok().build();
    }
}
