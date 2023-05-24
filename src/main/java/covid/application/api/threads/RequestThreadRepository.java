package covid.application.api.threads;

import java.util.ArrayList;
import java.util.List;

public class RequestThreadRepository {

    private static List<String> lista = new ArrayList<>();

    public static List<String> getLista() {
        return lista;
    }

    public static void limparLista(){
        lista.clear();
    }

    public static void addLista(String resultado){
        lista.add(resultado);
    }
}
