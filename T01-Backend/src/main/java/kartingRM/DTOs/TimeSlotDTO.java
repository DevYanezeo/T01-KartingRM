package kartingRM.DTOs;

import lombok.*;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {
    private Long bookingId;
    private String reservationCode;
    private String clientName;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> assignedKarts;
    private String status;
    private String pricingInfo;

    public static TimeSlotDTO createAvailableSlot(LocalTime time) {
        return TimeSlotDTO.builder()
                .startTime(time)
                .endTime(time.plusMinutes(30))
                .status("AVAILABLE")
                .build();
    }
}