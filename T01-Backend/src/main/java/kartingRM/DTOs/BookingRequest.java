package kartingRM.DTOs;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class BookingRequest {
    private Long ownerId;
    private List<Long> participantIds;
    private Integer duration;
    private Integer laps;
    private LocalDate date;
    private LocalTime startTime;
}

