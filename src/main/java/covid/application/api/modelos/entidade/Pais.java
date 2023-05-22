package covid.application.api.modelos.entidade;

import jakarta.persistence.*;

@Entity
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String sigla;

    private String nome;


    public Pais(String sigla, String nome) {
        this.sigla = sigla;
        this.nome = nome;
    }

    public Pais() {
    }

    public long getId() {
        return id;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
