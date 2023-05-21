package covid.application.api.modelos.entidade;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    String nome;

    String sigla;

    @ManyToOne
    @JoinColumn(name = "pais_id")
    private Pais pais;

    @OneToMany(mappedBy = "estado")
    private List<Cidade> cidades;

    public Estado(String nome, String sigla) {
        this.nome = nome;
        this.sigla = sigla;
    }

    public Estado() {
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public List<Cidade> getCidades() {
        return cidades;
    }

    public void setCidades(List<Cidade> cidades) {
        this.cidades = cidades;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }
}
