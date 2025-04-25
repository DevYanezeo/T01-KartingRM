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
    private Integer laps; // 10, 15 o 20

    @Column(nullable = false)
    private Double basePrice; // Precio base (días normales)

    @Column(nullable = false)
    private Integer totalDuration; // 30, 35 o 40 min

    @OneToMany(mappedBy = "pricing")
    private List<Booking> bookings = new ArrayList<>();

    public Double getPriceForDay(boolean isWeekend, boolean isHoliday) {
        double price = basePrice;
        if (isWeekend || isHoliday) {
            price *= 1.05; // +5% para fines de semana y feriados
        }
        return price;
    }
}