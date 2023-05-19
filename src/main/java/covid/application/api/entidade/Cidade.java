package covid.application.api.entidade;

import jakarta.persistence.*;

@Entity
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String cidade;

    private String provincia;

    @Column(name = "data_insercao")
    private String dataInsercao;

    private String latitude;

    private String longitude;

    public Localidade(String cidade, String provincia, String dataInsercao, String latitude, String longitude) {
        this.cidade = cidade;
        this.provincia = provincia;
        this.dataInsercao = dataInsercao;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Cidade() {

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
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
