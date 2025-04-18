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
    private String kartCode;

    private String model;
    private String status;

}
