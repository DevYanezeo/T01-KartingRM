package kartingRM.Entidades;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long id;
    private String nombreCliente;
    private String telefonoCliente;
    private String correoCliente;
    private int visitasMensual;
    private Date fechaNacimiento;

    public Cliente(){
    }
    public Cliente(String nombreCliente, String telefonoCliente, String correoCliente, int visitasMensual, Date fechaNacimiento) {
        this.nombreCliente = nombreCliente;
        this.telefonoCliente = telefonoCliente;
        this.correoCliente = correoCliente;
        this.visitasMensual = visitasMensual;
        this.fechaNacimiento = fechaNacimiento;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getNombreCliente() {
        return nombreCliente;
    }
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    public String getTelefonoCliente() {
        return telefonoCliente;
    }
    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }
    public String getCorreoCliente() {
        return correoCliente;
    }
    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }
    public int getVisitasMensual() {
        return visitasMensual;
    }
    public void setVisitasMensual(int visitasMensual) {
        this.visitasMensual = visitasMensual;
    }
    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }
    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
