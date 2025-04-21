package kartingRM.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pricing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer laps; // 10, 15 o 20 (PDF pág.3)

    @Column(nullable = false)
    private Integer maxMinutes; // 10, 15 o 20 (PDF pág.3)

    @Column(nullable = false)
    private Double regularPrice; // Precio días normales

    @Column(nullable = false)
    private Double weekendPrice; // Precio fin de semana (+20%)

    @Column(nullable = false)
    private Integer totalDuration; // 30, 35 o 40 min (PDF pág.3)

    // Relación con reservas
    @OneToMany(mappedBy = "pricing")
    private List<Booking> bookings = new ArrayList<>();

    public Double getPriceForDay(boolean isWeekend, boolean isHoliday) {
        return isHoliday ? weekendPrice * 1.2 : // +20% en feriados
                isWeekend ? weekendPrice :
                        regularPrice;
    }
}