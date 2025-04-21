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
    private String reservationCode; // RES-ABC123 (PDF pág.5)

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private Integer duration; // 30, 35 o 40 min (PDF pág.3)

    @Column(nullable = false)
    private Integer laps; // 10, 15 o 20 (PDF pág.3)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.CONFIRMED;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Client client;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingParticipant> participants = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "booking_karts",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "kart_code"))
    private List<Kart> assignedKarts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Pricing pricing;

    // Información de pago (PDF pág.5)
    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double totalDiscount;

    @Column(nullable = false)
    private Double tax;

    @Column(nullable = false)
    private Double total;

    // Días especiales (PDF pág.3)
    @Column(nullable = false)
    private Boolean isWeekend = false;

    @Column(nullable = false)
    private Boolean isHoliday = false;

    @Column(nullable = false)
    private Boolean hasBirthdayPromo = false;

    public enum Status {
        CONFIRMED, CANCELLED, COMPLETED
    }
}