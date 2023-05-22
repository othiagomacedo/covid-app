package covid.application.api.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import covid.application.api.modelos.enums.Requisicao;
import covid.application.api.modelos.records.DadosReportBuscaDataSigla;
import covid.application.api.modelos.records.DadosRespostaReportPais;

public class RequisicaoPorPais{

    public static DadosRespostaReportPais obterDadosPorDataSigla(DadosReportBuscaDataSigla dadosRecord) throws Exception {
        String url = null;
        DadosRespostaReportPais resposta = null;

        url = Requisicao.REPORT_TOTAL_DIA_PAIS_POR_DATA_SIGLA.get();
        DadosReportBuscaDataSigla dados = DadosReportBuscaDataSigla.obterCampos(dadosRecord);
        url = url.replace("{DATA}",dados.data()).replace("{SIGLA}",dados.sigla());
        String jsonResposta = Requests.realizarRequest(url);
        resposta = montarRecord(jsonResposta);

        return resposta;
    }

    private static DadosRespostaReportPais montarRecord(String respostaRequest) throws Exception {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(respostaRequest);
            JsonNode dataNode = jsonNode.get("data");
            DadosRespostaReportPais dados = new DadosRespostaReportPais(
                    dataNode.get("date").asText(),
                    dataNode.get("last_update").asText(),
                    dataNode.get("confirmed").asInt(),
                    dataNode.get("confirmed_diff").asInt(),
                    dataNode.get("deaths").asInt(),
                    dataNode.get("deaths_diff").asInt(),
                    dataNode.get("recovered").asInt(),
                    dataNode.get("recovered_diff").asInt(),
                    dataNode.get("active").asInt(),
                    dataNode.get("active_diff").asInt(),
                    dataNode.get("fatality_rate").asDouble()
            );
            return dados;
        }catch (Exception e){
            throw new Exception("Nao foi possível obter Record DadosRespostaReportPais a partir de String",e);
        }
    }


}
