package kartingRM.Entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Pricing")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int laps; // contador

    private Double basePrice;
    private Integer duration; // Minutos (PDF pág.3)

    // Para tarifas especiales (PDF pág.3)
    private Double weekendPrice;
    private Double holidayPrice;
}