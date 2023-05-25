package covid.application.api.modelos.records;

import covid.application.api.annotations.Sigla;

import java.lang.reflect.Field;

public record DadosPaisesSigla(
        String nome,

        @Sigla
        String sigla
) {
    public static <T extends Record> DadosPaisesSigla obterCampos(T generico) throws Exception {
        Field[] fields = generico.getClass().getDeclaredFields();
        String nome = null;
        String sigla = null;

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals("nome")) {
                nome = (String) field.get(generico);
            } else if (field.getName().equals("sigla")) {
                sigla = (String) field.get(generico);
            }
        }

        return new DadosPaisesSigla( nome, sigla);
    }
}

