package covid.application.api.entidade;

import jakarta.persistence.*;

@Entity
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nome;

    @Column(name = "data_insercao")
    private String dataInsercao;

    private String latitude;

    private String longitude;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private Estado estado;

    public Cidade(String nome, String dataInsercao, String latitude, String longitude, Estado estado) {
        this.nome = nome;
        this.dataInsercao = dataInsercao;
        this.latitude = latitude;
        this.longitude = longitude;
        this.estado = estado;
    }

    public Cidade() {

    public String getCidade() {
        return cidade;
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

    public String getDataInsercao() {
        return dataInsercao;
    }

    public void setDataInsercao(String dataInsercao) {
        this.dataInsercao = dataInsercao;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
