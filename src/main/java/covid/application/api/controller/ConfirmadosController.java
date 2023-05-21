package covid.application.api.controller;

import covid.application.api.modelos.enums.TipoLocalidade;
import covid.application.api.modelos.records.DadosBuscaLocalidade;
import covid.application.api.service.ConfirmadosService;
import covid.application.api.util.Print;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dados/confirmados")
public class ConfirmadosController {

    //Get Confirmados por um período de data
    @GetMapping("/{tipoLocalidade}/{nomeCidade}/{dataInicial}&{dataFinal}")
    public ResponseEntity obterConfimadosByData(@PathVariable("tipoLocalidade") @Pattern(regexp = "CIDADE|ESTADO|PAIS") TipoLocalidade tipoLocalidade,
                                                @PathVariable String nomeCidade,
                                                @PathVariable String dataInicial,
                                                @PathVariable String dataFinal,
                                                HttpServletRequest request){

        Print.request(request);
        return ConfirmadosService.obterConfimadosByData(new DadosBuscaLocalidade(tipoLocalidade,nomeCidade,dataInicial,dataFinal));
    }

}
