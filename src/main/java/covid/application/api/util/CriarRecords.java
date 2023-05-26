package covid.application.api.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import covid.application.api.modelos.entidade.Benchmark;
import covid.application.api.modelos.entidade.HistoricoPais;
import covid.application.api.modelos.records.DadosPaisesSigla;
import covid.application.api.modelos.records.DadosRespostaBenchmark;
import covid.application.api.modelos.records.DadosRespostaReportPais;

import java.util.ArrayList;
import java.util.List;

public class CriarRecords{

    public static DadosRespostaReportPais montarRecordRespostaDadosPais(String sigla, String respostaRequest) throws Exception {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(respostaRequest);
            JsonNode dataNode = jsonNode.get("data");
            DadosRespostaReportPais dados;
            if (dataNode.isEmpty()){
                dados = new DadosRespostaReportPais(
                        sigla,
                        "",
                        "",
                        "",
                        0,
                        0,
                        0,
                        0
                );
            } else {
                dados = new DadosRespostaReportPais(
                        sigla,
                        dataNode.get("date").asText(),
                        dataNode.get("last_update").asText(),
                        dataNode.get("last_update").asText(),
                        dataNode.get("confirmed").asInt(),
                        dataNode.get("deaths").asInt(),
                        dataNode.get("recovered").asInt(),
                        dataNode.get("fatality_rate").asDouble()
                );
            }
            return dados;
        }catch (Exception e){
            throw new Exception("Nao foi possível obter Record DadosRespostaReportPais a partir de String",e);
        }
    }

    public static DadosRespostaReportPais montarRecordRespostaDadosPais(String sigla, String respostaRequest, String dataFinal) throws Exception {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(respostaRequest);
            JsonNode dataNode = jsonNode.get("data");
            DadosRespostaReportPais dados;
            if (dataNode.isEmpty()){
                dados = new DadosRespostaReportPais(
                        sigla,
                        "",
                        "",
                        "",
                        0,
                        0,
                        0,
                        0
                );
            } else {
                dados = new DadosRespostaReportPais(
                        sigla,
                        dataNode.get("date").asText(),
                        dataFinal,
                        dataNode.get("last_update").asText(),
                        dataNode.get("confirmed").asInt(),
                        dataNode.get("deaths").asInt(),
                        dataNode.get("recovered").asInt(),
                        dataNode.get("fatality_rate").asDouble()
                );
            }
            return dados;
        }catch (Exception e){
            throw new Exception("Nao foi possível obter Record DadosRespostaReportPais a partir de String",e);
        }
    }

    public static DadosRespostaReportPais montarDadosRespostaReportByHistoricoPais(HistoricoPais hist){
        DadosRespostaReportPais dados = new DadosRespostaReportPais(
                hist.getPaisSigla(),
                hist.getDataInicial(),
                hist.getDataFinal(),
                hist.getUltimoUpdate(),
                hist.getConfirmados(),
                hist.getMortes(),
                hist.getRecuperados(),
                hist.getPercentualFatalidade()
        );

        return dados;
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

    public static DadosRespostaBenchmark montarDadosRespostaBenchmark(Benchmark benchmark) throws Exception{
        DadosRespostaBenchmark dados = new DadosRespostaBenchmark(
                benchmark.getId(),
                benchmark.getNomeHistorico(),
                benchmark.getDataHistorico(),
                benchmark.getDataIncial(),
                benchmark.getDataFinal(),
                benchmark.getConfirmadosDiferenca(),
                benchmark.getMortesDiferenca(),
                benchmark.getRecuperadosDiferenca(),
                montarDadosRespostaReportByHistoricoPais(benchmark.getHistoricoPais1()),
                montarDadosRespostaReportByHistoricoPais(benchmark.getHistoricoPais2()),
                true,
                null
        );
        return dados;
    }



}
