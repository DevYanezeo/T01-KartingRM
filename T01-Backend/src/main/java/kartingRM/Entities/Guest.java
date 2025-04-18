package kartingRM.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Obligatorio (PDF pág.5)
    private String email;

    private LocalDate birthDate; // Para validar descuento cumpleaños

    @Transient
    private Boolean isBirthday; // Calculado al crear reserva

    @ManyToOne
    private Booking booking;
}