package covid.application.api.util;

import covid.application.api.controller.ConfirmadosController;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Print {

    private static Logger LOG = LoggerFactory.getLogger(ConfirmadosController.class);

    //Exibir as requests recebidas no Controller
    public static void request(HttpServletRequest request){
        String resp ="Requisicao solicitada: ["+request.getMethod().toUpperCase()+"] -> "+request.getRequestURL().toString();

        String params = request.getParameterMap().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + Arrays.toString(entry.getValue()))
                .collect(Collectors.joining("\n"));

        LOG.info(resp);
        if (params.length() > 0) LOG.info("Parametros da requisição: "+ params);
    }

    public static void requestAndRecord(HttpServletRequest request, Record dados){
        String resposta ="Requisicao solicitada: ["+request.getMethod().toUpperCase()+"] -> "+request.getRequestURL().toString();

        String params = request.getParameterMap().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + Arrays.toString(entry.getValue()))
                .collect(Collectors.joining("\n"));

        LOG.info(resposta);
        if (params.length() > 0) LOG.info("Parametros da requisição: "+ params);

        String json = dados.toString();

        LOG.info("JSON recebido para a requisição: "+json);
    }
}
