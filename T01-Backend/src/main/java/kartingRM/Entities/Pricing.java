package kartingRM.Entities;

import jakarta.persistence.*;
import lombok.*;

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
    private Integer maxMinutes;

    @Column(nullable = false)
    private Double regularPrice;

    @Column(nullable = false)
    private Double weekendPrice;

    @Column(nullable = false)
    private Integer totalDuration;

    public Double getPriceForDay(boolean isWeekend, boolean isHoliday) {
        return isHoliday ? this.weekendPrice * 1.2 :
                isWeekend ? this.weekendPrice :
                        this.regularPrice;
    }
}