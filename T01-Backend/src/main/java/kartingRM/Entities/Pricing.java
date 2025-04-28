package kartingRM.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Integer laps;

    @Column(nullable = false)
    private Double basePrice;

    @Column(nullable = false)
    private Integer totalDuration;

    @OneToMany(mappedBy = "pricing")
    private List<Booking> bookings = new ArrayList<>();

    public Double getPriceForDay(boolean isWeekend, boolean isHoliday) {
        double price = basePrice;
        if (isWeekend || isHoliday) {
            price *= 1.05;
        }
        return price;
    }
}