package covid.application.api.controller;

import covid.application.api.modelos.records.DadosBuscaPaisDatas;
import covid.application.api.modelos.records.DadosReportBuscaDataSigla;
import covid.application.api.service.HistoricoPaisService;
import covid.application.api.util.Print;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dados")
public class CovidDadosController {

    final HistoricoPaisService covidDados;

    Print print = new Print(CovidDadosController.class);

    public CovidDadosController(HistoricoPaisService covidDados) {
        this.covidDados = covidDados;
    }

    @GetMapping("/total/{siglaPais}/{data}")
    public ResponseEntity obterDadosByData(@PathVariable String siglaPais,
                                           @PathVariable String data,
                                           HttpServletRequest request) throws Exception {

        print.request(request);
        return covidDados.obterDadosByData(new DadosReportBuscaDataSigla(data,siglaPais));
    }

    @GetMapping("/totais/{siglaPais}/{dataInicial}&{dataFinal}")
    public ResponseEntity obterDadosPaisPorFaixaDatas(@PathVariable String siglaPais,
                                                      @PathVariable String dataInicial,
                                                      @PathVariable String dataFinal,
                                                      HttpServletRequest request) throws Exception {
        print.request(request);
        return covidDados.obterDadosPaisPorFaixaDatas(new DadosBuscaPaisDatas(siglaPais, dataInicial, dataFinal));
    }

    @GetMapping("/all")
    public ResponseEntity obterTodosHistoricoPaises(HttpServletRequest request) throws Exception {
        print.request(request);
        return covidDados.obterTodosHistoricoPaises();
    }
}
