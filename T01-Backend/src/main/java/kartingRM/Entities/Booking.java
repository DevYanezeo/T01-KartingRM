package kartingRM.Entities;


import lombok.*;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String reservationCode; // Código único (PDF pág.5)

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    private Integer duration; // En minutos (PDF pág.3)

    private String status; // CONFIRMED, CANCELLED

    // Relaciones
    @ManyToOne
    private Client client;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Guest> guests;

    @ManyToMany
    private List<TimeSlot> timeSlots;

    @ManyToOne
    private Pricing pricing;

    // Campos calculados
    private Double subtotal;
    private Double totalDiscount;
    private Double tax;
    private Double total;

    // Para días especiales (PDF pág.3)
    private Boolean isWeekend;
    private Boolean isHoliday;
}
