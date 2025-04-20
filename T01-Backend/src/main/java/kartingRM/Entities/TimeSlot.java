package kartingRM.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dayOfWeek; // LUNES, MARTES...
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAvailable;
    auto_increment description not null
    entity

    private boolean isAvailable;

}