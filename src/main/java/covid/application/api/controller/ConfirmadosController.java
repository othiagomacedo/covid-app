package covid.application.api.controller;

import covid.application.api.records.DadosBuscaLocalidade;
import covid.application.api.service.ConfirmadosService;
import covid.application.api.util.Print;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/api/dados/confirmados")
public class ConfirmadosController {


    //Get Confirmados por range de data
    @GetMapping("/cidade={nome_cidade}/{data_inicial}&{data_final}")
    public ResponseEntity obterConfimadosCidadeByData(@PathVariable String nome_cidade,
                                                      @PathVariable String data_inicial,
                                                      @PathVariable String data_final,
                                                      HttpServletRequest request){

        Print.request(request);
        return ConfirmadosService.obterConfimadosCidadeByData(new DadosBuscaLocalidade(nome_cidade,data_inicial,data_final));
    }

    @GetMapping("/estado={nome_estado}/{data_inicial}&{data_final}")
    public ResponseEntity obterConfimadosEstadoByData(@PathVariable String nome_estado,
                                                      @PathVariable String data_inicial,
                                                      @PathVariable String data_final,
                                                      HttpServletRequest request){

        Print.request(request);
    }

    @GetMapping("/pais={nome_pais}/{data_inicial}&{data_final}")
    public ResponseEntity obterConfimadosPaisByData(@PathVariable String nome_pais,
                                                    @PathVariable String data_inicial,
                                                    @PathVariable String data_final,
                                                    HttpServletRequest request){

        Print.request(request);
    }


}
