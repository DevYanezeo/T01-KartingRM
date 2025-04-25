package kartingRM.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 12)
    private String reservationCode; // Ej: RES-ABC123

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer duration; // En minutos: 30, 35, 40

    @Column(nullable = false)
    private Integer laps; // 10, 15 o 20

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.CONFIRMED;

    // Cliente responsable de la reserva (el que paga)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Client owner;

    // Participantes de la reserva (incluye al owner si se desea)
    @ManyToMany
    @JoinTable(
            name = "booking_participants",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private List<Client> participants = new ArrayList<>();

    // Karts asignados a esta reserva
    @ManyToMany
    @JoinTable(
            name = "booking_karts",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "kart_code")
    )
    private List<Kart> assignedKarts = new ArrayList<>();

    // Tarifa asociada a esta reserva
    @ManyToOne
    @JoinColumn(nullable = false)
    private Pricing pricing;

    // Relación con factura (opcional si aún no se ha emitido)
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Invoice invoice;

    @PrePersist
    @PreUpdate
    private void calculateEndTime() {
        if (startTime != null && duration != null) {
            this.endTime = startTime.plusMinutes(duration);
        }
    }

    public enum Status {
        CONFIRMED, CANCELLED, COMPLETED
    }
}
