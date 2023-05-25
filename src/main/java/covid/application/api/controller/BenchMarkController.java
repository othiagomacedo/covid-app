package covid.application.api.controller;

import covid.application.api.modelos.records.DadosBuscaBenchmark;
import covid.application.api.service.BenchMarkService;
import covid.application.api.util.Print;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bench")
public class BenchMarkController {


    final BenchMarkService bench;

    Print print = new Print(BenchMarkController.class);

    public BenchMarkController(BenchMarkService bench) {
        this.bench = bench;
    }

    @GetMapping("/get/{paisSigla1}&{paisSigla2}/{dataInicial}&{dataFinal}/{nomebench}")
    public ResponseEntity obterBenchmark(@PathVariable String paisSigla1,
                                         @PathVariable String paisSigla2,
                                         @PathVariable String dataInicial,
                                         @PathVariable String dataFinal,
                                         @PathVariable String nomebench,
                                         HttpServletRequest request) throws Exception {

        print.request(request);
        return bench.obterBenchmark(new DadosBuscaBenchmark(nomebench,paisSigla1,paisSigla2,dataInicial,dataFinal));
    }


}
