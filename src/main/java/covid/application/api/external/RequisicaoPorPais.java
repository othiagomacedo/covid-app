package covid.application.api.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import covid.application.api.modelos.records.DadosPaisesSigla;
import covid.application.api.modelos.records.DadosRespostaReportPais;

import java.util.ArrayList;
import java.util.List;

public class RequisicaoPorPais{

    public static DadosRespostaReportPais montarRecordRespostaDadosPais(String respostaRequest) throws Exception {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(respostaRequest);
            JsonNode dataNode = jsonNode.get("data");
            DadosRespostaReportPais dados = new DadosRespostaReportPais(
                    "",
                    dataNode.get("date").asText(),
                    dataNode.get("last_update").asText(),
                    dataNode.get("confirmed").asInt(),
                    dataNode.get("deaths").asInt(),
                    dataNode.get("recovered").asInt(),
                    dataNode.get("fatality_rate").asDouble()
            );
            return dados;
        }catch (Exception e){
            throw new Exception("Nao foi possível obter Record DadosRespostaReportPais a partir de String",e);
        }
    }

    public static List<DadosPaisesSigla> montarRecordPaisesSigla(String respostaRequest)throws Exception{
        try{
            List<DadosPaisesSigla> lista = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(respostaRequest);
            JsonNode dataNode = jsonNode.get("data");
            for (JsonNode no : dataNode){
                DadosPaisesSigla dados = new DadosPaisesSigla(
                        no.get("name").asText(),
                        no.get("iso").asText()
                );
                lista.add(dados);
            }
            return lista;
        }catch (Exception e){
            throw new Exception("Nao foi possível obter Record DadosPaisesSigla a partir de String",e);
        }

    }


}
