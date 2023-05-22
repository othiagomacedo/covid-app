package covid.application.api.controller;

import covid.application.api.modelos.enums.Covid;
import covid.application.api.modelos.records.DadosBuscaLocalidade;
import covid.application.api.service.CovidDadosService;
import covid.application.api.util.Print;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dados")
public class CovidDadosController {

    Print print = new Print(CovidDadosController.class);

    @GetMapping("/{tipoDadosCovid}/{nomeLocal}/{dataInicial}&{dataFinal}")
    public ResponseEntity obterDadosByData(@PathVariable("tipoDadosCovid") @Pattern(regexp = "CONFIRMADOS|MORTOS|RECUPERADOS") Covid tipoDadosCovid,
                                           @PathVariable String nomeLocal,
                                           @PathVariable String dataInicial,
                                           @PathVariable String dataFinal,
                                           HttpServletRequest request){

        print.request(request);
        return CovidDadosService.obterDadosByData(new DadosBuscaLocalidade(tipoDadosCovid,nomeLocal,dataInicial,dataFinal));
    }
}
