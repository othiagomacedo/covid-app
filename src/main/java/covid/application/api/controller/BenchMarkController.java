package covid.application.api.controller;

import covid.application.api.modelos.records.DadosBuscaBenchmark;
import covid.application.api.modelos.records.DadosEdicaoBenchmark;
import covid.application.api.modelos.records.DadosExcluirBench;
import covid.application.api.service.BenchMarkService;
import covid.application.api.util.Print;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bench")
@CrossOrigin(origins = "*")
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
        return bench.obterBenchmark(new DadosBuscaBenchmark(nomebench, paisSigla1, paisSigla2, dataInicial, dataFinal));
    }

    @GetMapping("/get/id={id}")
    public ResponseEntity obterBenchmarkByID(@PathVariable long id,HttpServletRequest request) throws Exception {
        print.request(request);
        return bench.obterBenchPeloID(id);
    }

    @GetMapping("/get/all")
    public ResponseEntity obterTodosBenchmark(HttpServletRequest request) {
        print.request(request);
        return bench.obterTodosBenchmarks();
    }

    @Transactional
    @PostMapping("/edit/{id}/{paisSigla1}&{paisSigla2}/{dataInicial}&{dataFinal}/{nomebench}")
    public ResponseEntity editBenchmark(@PathVariable long id,
                                        @PathVariable String paisSigla1,
                                        @PathVariable String paisSigla2,
                                        @PathVariable String dataInicial,
                                        @PathVariable String dataFinal,
                                        @PathVariable String nomebench,
                                        HttpServletRequest request) throws Exception {
        print.request(request);
        return bench.editarBenchmark(new DadosEdicaoBenchmark(id,nomebench,paisSigla1, paisSigla2, dataInicial, dataFinal));
    }

    @Transactional
    @DeleteMapping("/del/nome={nomeBench}")
    public ResponseEntity apagarBenchmark(@PathVariable String nomeBench, HttpServletRequest request) throws Exception {
        print.request(request);
        return bench.deletarBenchPeloNome(new DadosExcluirBench(0, nomeBench));
    }

    @Transactional
    @DeleteMapping("/del/id={id}")
    public ResponseEntity apagarBenchmark(@PathVariable long id, HttpServletRequest request) throws Exception {
        print.request(request);
        return bench.deletarBenchPeloId(new DadosExcluirBench(id, ""));
    }
}
