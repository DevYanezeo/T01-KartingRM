package kartingRM.Services;

import kartingRM.Entities.BusinessHour;
import kartingRM.Repositories.BusinessHourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Service
public class BusinessHourService {

    @Autowired
    private BusinessHourRepository businessHourRepository;

    public boolean isWithinBusinessHours(LocalDate date, LocalTime startTime, LocalTime endTime) {
        DayOfWeek day = date.getDayOfWeek();
        List<BusinessHour> hoursList = businessHourRepository.findByDayOfWeek(day);

        if (hoursList.isEmpty()) {
            throw new RuntimeException("No business hours configured for " + day);
        }

        BusinessHour hours = hoursList.get(0);

        return !startTime.isBefore(hours.getOpeningTime()) && !endTime.isAfter(hours.getClosingTime());
    }
}
