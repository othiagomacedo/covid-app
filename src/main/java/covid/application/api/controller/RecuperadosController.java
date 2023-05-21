package covid.application.api.controller;

import covid.application.api.modelos.enums.TipoLocalidade;
import covid.application.api.modelos.records.DadosBuscaLocalidade;
import covid.application.api.service.RecuperadosService;
import covid.application.api.util.Print;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dados/recuperados")
public class RecuperadosController {

    //Get Recuperados por um período de data
    @GetMapping("/{tipoLocalidade}/{nomeCidade}/{dataInicial}&{dataFinal}")
    public ResponseEntity obterRecupCidadeByData(@PathVariable("tipoLocalidade") @Pattern(regexp = "CIDADE|ESTADO|PAIS") TipoLocalidade tipoLocalidade,
                                                 @PathVariable String nomeCidade,
                                                 @PathVariable String dataInicial,
                                                 @PathVariable String dataFinal,
                                                 HttpServletRequest request){
        Print.request(request);
        return RecuperadosService.obterRecupCidadeByData(new DadosBuscaLocalidade(tipoLocalidade,nomeCidade,dataInicial,dataFinal));
    }

}
