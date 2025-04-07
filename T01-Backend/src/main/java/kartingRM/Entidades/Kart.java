package kartingRM.Entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "Kart")

public class Kart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long id;
    private String codigo;
    private String modelo;
    private String estado;

    public Kart(){

    }
    public Kart(String codigo, String modelo, String estado) {
        this.codigo = codigo;
        this.modelo = modelo;
        this.estado = estado;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

}
