package covid.application.api.controller;

import covid.application.api.service.PaisService;
import covid.application.api.util.Print;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pais")
public class PaisController {

    final PaisService pais;

    Print print = new Print(PaisController.class);

    public PaisController(PaisService pais) {
        this.pais = pais;
    }

    @GetMapping
    public ResponseEntity obterTodosPaises(HttpServletRequest request) throws Exception {
        print.request(request);
        return pais.getTodosPaisesCadastrados();
    }

    @GetMapping("/nome={nome}")
    public ResponseEntity obterPaisByNome(@PathVariable String nome, HttpServletRequest request) throws Exception {
        print.request(request);
        return pais.obterPaisByNome(nome);
    }

    @GetMapping("/sigla={sigla}")
    public ResponseEntity obterPaisBySigla(@PathVariable String sigla, HttpServletRequest request) throws Exception{
        print.request(request);
        return pais.obterPaisBySigla(sigla);
    }
}
