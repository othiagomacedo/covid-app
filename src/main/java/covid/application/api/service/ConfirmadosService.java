package covid.application.api.service;

import covid.application.api.records.DadosBuscaLocalidade;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ConfirmadosService {

    public static ResponseEntity obterConfimadosCidadeByData(DadosBuscaLocalidade dados){

        return ResponseEntity.ok().build();
    }

    public static ResponseEntity obterConfimadosEstadoByData(DadosBuscaLocalidade dados){
        if (dados.nomeLocalidade().trim().length() == 2){

        }
        return ResponseEntity.ok().build();
    }

    public static ResponseEntity obterConfimadosPaisByData(DadosBuscaLocalidade dados){

        return ResponseEntity.ok().build();
    }
}
