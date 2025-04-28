package kartingRM.Controllers;

import kartingRM.DTOs.WeeklyRackRequest;
import kartingRM.DTOs.WeeklyRackResponse;
import kartingRM.Services.WeeklyRackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class WeeklyRackController {

    private final WeeklyRackService weeklyRackService;

    @GetMapping("/weekly")
    public WeeklyRackResponse getWeeklyRack(@ModelAttribute WeeklyRackRequest request) {
        return weeklyRackService.generateWeeklyRack(request);
    }
}