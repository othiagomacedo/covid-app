package covid.application.api.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Requests {

    private static Logger LOG = LoggerFactory.getLogger(Requests.class);

    public static String realizarRequest(String urlDaAPI) throws Exception{
        try{
            LOG.info("Irei realizar uma requisicao a API externa.");
            LOG.info("URL da API externa: " + urlDaAPI);

            java.net.URL url = new URL(urlDaAPI);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int codigoResposta = connection.getResponseCode();
            LOG.info("Código da resposta: " + codigoResposta);

            if (codigoResposta == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder resposta = new StringBuilder();

                String linha;
                while ((linha = br.readLine()) != null) {
                    resposta.append(linha);
                }
                br.close();

                LOG.info("Resposta da API : \n" + resposta);
                return resposta.toString();
            } else {
                throw new Exception("Não foi possível realizar a requisicao. ");
            }

        }catch (Exception e){
            LOG.error("Não foi possível realizar a requisicao. ",e);
            throw e;
        }
    }
}

