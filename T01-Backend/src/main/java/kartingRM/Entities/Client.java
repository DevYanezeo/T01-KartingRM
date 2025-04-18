package kartingRM.Entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email; // Para comprobante (PDF pág.5)

    private LocalDate birthDate; // Para descuento si cumple años (PDF pág.3)

    @Transient // No se persiste, se calcula
    private Integer monthlyVisits; // Para descuento frecuente (PDF pág.3)
}
