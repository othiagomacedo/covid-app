package covid.application.api.modelos.entidade;

import covid.application.api.modelos.records.DadosPaisesSigla;
import jakarta.persistence.*;

@Entity
@Table(name = "pais", uniqueConstraints = @UniqueConstraint(columnNames = "sigla"))
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String sigla;

    private String nome;


    public Pais(String sigla, String nome) {
        this.sigla = sigla;
        this.nome = nome;
    }

    public Pais(DadosPaisesSigla dados){
        this.sigla = dados.sigla().toUpperCase();
        this.nome = dados.nome().toUpperCase();
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
