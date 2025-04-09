package kartingRM.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Kart")
public class Kart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private String kartCode;
    private String model;
    private String status;

    public Kart() {
    }

    public Kart(String kartCode, String model, String status) {
        this.kartCode = kartCode;
        this.model = model;
        this.status = status;
    }

    public String getKartCode() {
        return kartCode;
    }

    public void setCode(String kartCode) {
        this.kartCode = kartCode;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
