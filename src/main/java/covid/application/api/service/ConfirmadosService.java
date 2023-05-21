package covid.application.api.service;

import covid.application.api.modelos.records.DadosBuscaLocalidade;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ConfirmadosService {

    public static ResponseEntity obterConfimadosByData(DadosBuscaLocalidade dados){

        return ResponseEntity.ok().build();
    }

}
