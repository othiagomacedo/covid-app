package covid.application.api.modelos.enums;

public enum Requisicao {
    REPORT_TOTAL_DIA_PAIS_POR_DATA_SIGLA {
        @Override
        public String get() {
            return "https://covid-api.com/api/reports/total?date={DATA}&iso={SIGLA}";
        }
    },

    OBTER_TODOS_PAISES_E_SIGLAS {
        @Override
        public String get() {
            return "https://covid-api.com/api/regions?per_page=300&order=iso&sort=asc";
        }
    };

    public abstract String get();
}
