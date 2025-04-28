package kartingRM.Services;

import kartingRM.DTOs.*;
import kartingRM.Entities.Booking;
import kartingRM.Repositories.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyRackService {
    private final BookingRepository bookingRepository;
    private final KartServices kartServices;
    private final ClientServices clientServices;

    public WeeklyRackResponse generateWeeklyRack(WeeklyRackRequest request) {
        LocalDate startDate = calculateStartDate(request);
        LocalDate endDate = startDate.plusDays(6);

        List<Booking> bookings = getFilteredBookings(startDate, endDate, request.getKartFilter());
        List<LocalTime> timeSlots = generateTimeSlots();

        return WeeklyRackResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .daysHeader(generateDaysHeader(startDate))
                .timeSlots(timeSlots)
                .calendarGrid(buildCalendarGrid(startDate, endDate, timeSlots, bookings))
                .build();
    }

    private LocalDate calculateStartDate(WeeklyRackRequest request) {
        LocalDate baseDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();
        return request.getWeeksOffset() != null ?
                baseDate.plusWeeks(request.getWeeksOffset()) :
                baseDate;
    }

    private List<Booking> getFilteredBookings(LocalDate startDate, LocalDate endDate, String kartFilter) {
        if (kartFilter != null && !kartFilter.isEmpty()) {
            return bookingRepository.findByDateBetweenAndKart(startDate, endDate, kartFilter);
        }
        return bookingRepository.findByDateBetweenWithDetails(startDate, endDate);
    }

    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime time = LocalTime.of(9, 0);
        while (time.isBefore(LocalTime.of(22, 30))) {
            slots.add(time);
            time = time.plusMinutes(60);
        }
        return slots;
    }

    private List<String> generateDaysHeader(LocalDate startDate) {
        return startDate.datesUntil(startDate.plusDays(7))
                .map(date -> {
                    String dayName = date.getDayOfWeek().toString().substring(0, 1) +
                            date.getDayOfWeek().toString().substring(1).toLowerCase();
                    return dayName + " " + date.getDayOfMonth();
                })
                .collect(Collectors.toList());
    }

    private Map<LocalTime, Map<LocalDate, List<TimeSlotDTO>>> buildCalendarGrid(
            LocalDate startDate, LocalDate endDate,
            List<LocalTime> timeSlots, List<Booking> bookings) {

        Map<LocalTime, Map<LocalDate, List<TimeSlotDTO>>> grid = new LinkedHashMap<>();

        timeSlots.forEach(time -> {
            Map<LocalDate, List<TimeSlotDTO>> dayMap = new LinkedHashMap<>();
            startDate.datesUntil(endDate.plusDays(1)).forEach(date -> {
                dayMap.put(date, new ArrayList<>(Collections.singletonList(
                        TimeSlotDTO.createAvailableSlot(time)
                )));
            });
            grid.put(time, dayMap);
        });

        bookings.forEach(booking -> processBooking(grid, booking));

        return grid;
    }

    private void processBooking(
            Map<LocalTime, Map<LocalDate, List<TimeSlotDTO>>> grid,
            Booking booking) {

        LocalDate date = booking.getDate();
        booking.getAssignedKarts().forEach(kart -> {
            markBookingSlots(grid, booking, date);
        });
    }

    private void markBookingSlots(
            Map<LocalTime, Map<LocalDate, List<TimeSlotDTO>>> grid,
            Booking booking, LocalDate date) {

        grid.forEach((time, dayMap) -> {
            if (!time.isBefore(booking.getStartTime()) && time.isBefore(booking.getEndTime())) {
                dayMap.get(date).clear();
                dayMap.get(date).add(convertToTimeSlotDTO(booking));
            }
        });
    }

    private TimeSlotDTO convertToTimeSlotDTO(Booking booking) {
        return TimeSlotDTO.builder()
                .bookingId(booking.getId())
                .reservationCode(booking.getReservationCode())
                .clientName(booking.getOwner().getName())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .assignedKarts(booking.getAssignedKarts().stream()
                        .map(kart -> kart.getKartCode())
                        .collect(Collectors.toList()))
                .status("BOOKED")
                .pricingInfo(String.format("%d vueltas - %d min",
                        booking.getPricing().getLaps(),
                        booking.getPricing().getTotalDuration()))
                .build();
    }
}