package kartingRM.DTOs;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
@Builder

public class WeeklyRackResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> daysHeader;
    private List<LocalTime> timeSlots;
    private Map<LocalTime, Map<LocalDate, List<TimeSlotDTO>>> calendarGrid;
}