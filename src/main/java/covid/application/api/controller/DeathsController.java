package covid.application.api.controller;

import covid.application.api.util.Print;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/dados/mortos")
public class DeathsController {

    //Get Mortos por range de data
    @GetMapping("/cidade={nome_cidade}/{data_inicial}&{data_final}")
    public ResponseEntity obterMortosCidadeByData(@PathVariable String nome_cidade,
                                                  @PathVariable String data_inicial,
                                                  @PathVariable String data_final,
                                                  HttpServletRequest request){
        Print.request(request);
    }

    @GetMapping("/estado={nome_estado}/{data_inicial}&{data_final}")
    public ResponseEntity obterMortosEstadoByData(@PathVariable String nome_estado,
                                                  @PathVariable String data_inicial,
                                                  @PathVariable String data_final,
                                                  HttpServletRequest request){
        Print.request(request);
    }

    @GetMapping("/pais={nome_pais}/{data_inicial}&{data_final}")
    public ResponseEntity obterMortosPaisByData(@PathVariable String nome_pais,
                                                @PathVariable String data_inicial,
                                                @PathVariable String data_final,
                                                HttpServletRequest request){
        Print.request(request);
    }
}
