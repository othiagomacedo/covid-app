package covid.application.api.controller;

import covid.application.api.util.Print;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/dados/recuperados")
public class RecuperadosController {

    //Get Recuperados por range de data
    @GetMapping("/cidade={nome_cidade}/{data_inicial}&{data_final}")
    public ResponseEntity obterRecupCidadeByData(@PathVariable String nome_cidade,
                                                 @PathVariable String data_inicial,
                                                 @PathVariable String data_final,
                                                 HttpServletRequest request){
        Print.request(request);
    }

    @GetMapping("/estado={nome_estado}/{data_inicial}&{data_final}")
    public ResponseEntity obterRecupEstadoByData(@PathVariable String nome_estado,
                                                 @PathVariable String data_inicial,
                                                 @PathVariable String data_final,
                                                 HttpServletRequest request){
        Print.request(request);
    }

    @GetMapping("/pais={nome_pais}/{data_inicial}&{data_final}")
    public ResponseEntity obterRecupPaisByData(@PathVariable String nome_pais,
                                               @PathVariable String data_inicial,
                                               @PathVariable String data_final,
                                               HttpServletRequest request){
        Print.request(request);
    }
}
