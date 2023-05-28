CREATE TABLE historico_pais (
        id SERIAL PRIMARY KEY,
        data_inicial VARCHAR(40),
        data_final VARCHAR(40),
        confirmados BIGINT,
        mortes BIGINT,
        recuperados BIGINT,
        ultimo_update VARCHAR(40),
        percentual_fatalidade FLOAT,
        pais_id BIGINT,
        FOREIGN KEY (pais_id) REFERENCES pais (id)
);

CREATE TABLE pais (
      id SERIAL PRIMARY KEY,
      sigla VARCHAR(5) UNIQUE,
      nome VARCHAR(50)
);

CREATE TABLE historico_benchmark (
     id SERIAL PRIMARY KEY,
     historico_pais1_id BIGINT,
     historico_pais2_id BIGINT,
     data_historico VARCHAR(40),
     FOREIGN KEY (historico_pais1_id) REFERENCES historico_pais (id),
     FOREIGN KEY (historico_pais2_id) REFERENCES historico_pais (id)
);
