package kartingRM.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking_participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Client client;

    @Column(nullable = false)
    private Boolean isBirthday = false; // Para descuento 50% (PDF pág.3)

    @Column(nullable = false)
    private Double finalPrice; // Precio individual con descuentos

    // Campos requeridos para comprobante (PDF pág.5)
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;
}