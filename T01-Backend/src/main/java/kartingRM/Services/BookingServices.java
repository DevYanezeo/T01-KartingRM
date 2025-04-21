package kartingRM.Services;

import kartingRM.Entities.*;
import kartingRM.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServices {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private KartRepository kartRepository;
    @Autowired
    private PricingRepository pricingRepository;
    @Autowired
    private DiscountServices discountService;
    @Autowired
    private BookingParticipantServices bookingParticipantServices;
    @Autowired
    private InvoiceServices invoiceService;

    @Transactional
    public Booking createBooking(Booking booking, List<Long> participantIds) {
        Client client = clientRepository.findById(booking.getClient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        Pricing pricing = pricingRepository.findByLaps(booking.getLaps())
                .orElseThrow(() -> new IllegalArgumentException("Tarifa no válida"));

        booking.setReservationCode(generateReservationCode());
        booking.setClient(client);
        booking.setPricing(pricing);
        booking.setDuration(pricing.getTotalDuration());

        booking.setIsWeekend(isWeekend(booking.getDate()));
        booking.setIsHoliday(isHoliday(booking.getDate()));

        validateBookingAvailability(booking);

        assignKarts(booking, participantIds.size());

        processParticipants(booking, participantIds);

        calculatePricing(booking);

        Booking savedBooking = bookingRepository.save(booking);

        invoiceService.generateInvoice(savedBooking);

        return savedBooking;
    }

    private String generateReservationCode() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private boolean isHoliday(LocalDate date) {
        // Implementar lógica para verificar feriados
        return false;
    }

    private void validateBookingAvailability(Booking booking) {
        // Verificar horario de atención
        LocalTime startTime = booking.getStartTime();
        LocalTime endTime = startTime.plusMinutes(booking.getDuration());

        DayOfWeek dayOfWeek = booking.getDate().getDayOfWeek();
        boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;

        LocalTime openingTime = isWeekend ? LocalTime.of(10, 0) : LocalTime.of(14, 0);
        LocalTime closingTime = LocalTime.of(22, 0);

        if (startTime.isBefore(openingTime) || endTime.isAfter(closingTime)) {
            throw new IllegalStateException("Fuera del horario de atención");
        }

        // Verificar disponibilidad de pista
        boolean exists = bookingRepository.existsOverlappingBooking(
                booking.getDate(),
                booking.getStartTime(),
                booking.getDuration());

        if (exists) {
            throw new IllegalStateException("Ya existe una reserva en ese horario");
        }
    }

    private void assignKarts(Booking booking, int kartsNeeded) {
        List<Kart> availableKarts = kartRepository.findAvailableKarts(
                booking.getDate(),
                booking.getStartTime(),
                booking.getDuration());

        if (availableKarts.size() < kartsNeeded) {
            throw new IllegalStateException("No hay suficientes karts disponibles");
        }

        booking.setAssignedKarts(availableKarts.stream()
                .limit(kartsNeeded)
                .collect(Collectors.toList()));
    }

    private void processParticipants(Booking booking, List<Long> participantIds) {
        int maxBirthdayDiscounts = calculateMaxBirthdayDiscounts(participantIds.size());
        int appliedBirthdayDiscounts = 0;

        for (Long clientId : participantIds) {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + clientId));

            BookingParticipant participant = new BookingParticipant();
            participant.setBooking(booking);
            participant.setClient(client);
            participant.setName(client.getName());
            participant.setEmail(client.getEmail());

            if (client.isBirthdayToday() && appliedBirthdayDiscounts < maxBirthdayDiscounts) {
                participant.setIsBirthday(true);
                appliedBirthdayDiscounts++;
            }

            bookingParticipantServices.saveParticipant(participant);
        }

        booking.setHasBirthdayPromo(appliedBirthdayDiscounts > 0);
    }

    private int calculateMaxBirthdayDiscounts(int groupSize) {
        return groupSize >= 6 ? 2 : groupSize >= 3 ? 1 : 0;
    }

    private void calculatePricing(Booking booking) {
        double basePrice = booking.getPricing().getPriceForDay(
                booking.getIsWeekend(),
                booking.getIsHoliday());

        int groupSize = booking.getParticipants().size();
        booking.setSubtotal(basePrice * groupSize);

        // Aplicar descuentos
        double groupDiscount = discountService.getGroupDiscount(groupSize);
        double frequentDiscount = booking.getClient().getMonthlyVisits() >= 2 ?
                discountService.getFrequentClientDiscount(booking.getClient().getMonthlyVisits()) : 0.0;
        double birthdayDiscount = booking.getHasBirthdayPromo() ?
                discountService.getBirthdayDiscount() : 0.0;

        double totalDiscountPercent = groupDiscount + frequentDiscount + birthdayDiscount;
        double totalDiscountAmount = booking.getSubtotal() * totalDiscountPercent;
        booking.setTotalDiscount(totalDiscountAmount);

        // Calcular impuestos y total
        double taxableAmount = booking.getSubtotal() - totalDiscountAmount;
        booking.setTax(taxableAmount * 0.19); // IVA 19%
        booking.setTotal(taxableAmount + booking.getTax());
    }

    public List<Booking> getWeeklySchedule(LocalDate startDate) {
        return bookingRepository.findByDateBetween(startDate, startDate.plusDays(6));
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        booking.setStatus(Booking.Status.CANCELLED);
        return bookingRepository.save(booking);
    }

    public Booking completeBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        booking.setStatus(Booking.Status.COMPLETED);

        // Incrementar visitas para los clientes
        booking.getParticipants().forEach(participant -> {
            Client client = participant.getClient();
            client.incrementMonthlyVisits();
            clientRepository.save(client);
        });

        return bookingRepository.save(booking);
    }

    public Optional<Invoice> getBookingInvoice(Long bookingId) {
        return invoiceService.findByBookingId(bookingId);
    }
}