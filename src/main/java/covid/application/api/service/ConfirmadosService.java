package covid.application.api.service;

import covid.application.api.records.DadosBuscaLocalidade;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ConfirmadosService {

    public static ResponseEntity obterConfimadosCidadeByData(DadosBuscaLocalidade dados){

        return ResponseEntity.ok().build();
    }
}
