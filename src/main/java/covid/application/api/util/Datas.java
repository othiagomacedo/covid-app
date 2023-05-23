package covid.application.api.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
}
