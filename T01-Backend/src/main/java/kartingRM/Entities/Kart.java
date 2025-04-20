package kartingRM.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Kart")
public class Kart {
    @Id
    @Column(name = "kart_code")
    private String kartCode;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private boolean underMaintenance;

    private String description;

}
