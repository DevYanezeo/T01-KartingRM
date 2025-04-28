package kartingRM.DTOs;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class BookingDTO {
    private Long id;
    private String reservationCode;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String ownerName;
    private List<String> assignedKarts;
    private Integer laps;
    private Integer duration;
}