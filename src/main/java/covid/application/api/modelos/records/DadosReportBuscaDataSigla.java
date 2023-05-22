package covid.application.api.modelos.records;

import java.lang.reflect.Field;

public record DadosReportBuscaDataSigla(
        String data,
        String sigla
) {
    public static <T extends Record> DadosReportBuscaDataSigla obterCampos(T generico) throws Exception {
        Field[] fields = generico.getClass().getDeclaredFields();
        String data = null;
        String sigla = null;

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals("data")) {
                data = field.get(generico).toString();
            } else if (field.getName().equals("sigla")) {
                sigla = field.get(generico).toString();
            }
        }

        return new DadosReportBuscaDataSigla(data, sigla);
    }
}
