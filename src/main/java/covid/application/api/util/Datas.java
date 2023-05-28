package covid.application.api.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Datas {

    public static boolean isSequencial(String data1, String data2) {
        LocalDate date1 = LocalDate.parse(data1);
        LocalDate date2 = LocalDate.parse(data2);
        return date1.isBefore(date2);
    }

    public static List<String> obterDatas(String dataInicial, String dataFinal) {
        LocalDate dataIni = LocalDate.parse(dataInicial);
        LocalDate dataFin = LocalDate.parse(dataFinal);
        List<String> todasDatas  = new ArrayList<>();

        todasDatas.add(dataIni.toString());

        LocalDate dataAtual = dataIni.plusDays(1);
        while (!dataAtual.isAfter(dataFin)) {
            todasDatas.add(dataAtual.toString());
            dataAtual = dataAtual.plusDays(1);
        }

        return todasDatas;
    }

    public static boolean isCorreto(String data){
        String padrao = "\\d{4}-\\d{2}-\\d{2}"; // Expressão regular para o formato yyyy-mm-dd
        Pattern pattern = Pattern.compile(padrao);
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }
}
