package covid.application.api.entidade;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String sigla;

    private String nome;

    @OneToMany(mappedBy = "pais")
    private List<Estado> estados;

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
