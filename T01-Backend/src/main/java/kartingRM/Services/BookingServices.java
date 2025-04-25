package kartingRM.Services;

import jakarta.transaction.Transactional;
import kartingRM.Entities.*;
import kartingRM.Repositories.*;
import kartingRM.DTOs.BookingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServices {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private KartRepository kartRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PricingRepository pricingRepository;

    @Autowired
    private DiscountServices discountServices;

    @Autowired
    private BookingRepository bookingRepository;
    private static final double WEEKEND_SURCHARGE = 0.05; // +5%
    private static final double HOLIDAY_SURCHARGE = 0.05; // +5%

    public Booking createBooking(BookingRequest request) {
        // 1. Validar y obtener datos básicos
        Client owner = validateOwner(request.getOwnerId());
        List<Client> participants = validateParticipants(request.getParticipantIds());
        Pricing pricing = validatePricing(request.getLaps(), request.getDuration());

        List<Kart> karts = assignAvailableKarts(
                request.getDate(),
                request.getStartTime(),
                request.getDuration(),
                participants.size() + 1 // owner + participants
        );

        // 2. Preparar lista completa de participantes
        List<Client> allParticipants = prepareParticipants(owner, participants);
        int totalPeople = allParticipants.size();

        // 3. Incrementar visitas de clientes
        incrementVisits(owner, participants);

        // 4. Calcular precio base con posibles recargos
        boolean isWeekend = isWeekend(request.getDate());
        boolean isHoliday = isHoliday(request.getDate());
        double basePrice = calculateFinalBasePrice(pricing.getBasePrice(), isWeekend, isHoliday);

        // Aplicar descuento por tamaño de grupo
        double groupDiscountRate = discountServices.getApplicableGroupDiscount(totalPeople);
        double priceAfterGroupDiscount = pricing.getBasePrice() * (1 - groupDiscountRate);

        // Descuento por cliente frecuente (solo para el dueño)
        double frequentDiscountRate = discountServices.getApplicableFrequentClientDiscount(owner.getMonthlyVisits());
        double ownerPrice = priceAfterGroupDiscount * (1 - frequentDiscountRate);

        // Descuento por cumpleaños (Lógica fija)
        double birthdayDiscountRate = discountServices.getBirthdayDiscount();
        long birthdayPeople = countBirthdayPeople(allParticipants);
        double totalBirthdayDiscount = birthdayPeople * priceAfterGroupDiscount * birthdayDiscountRate;


        // 6. Calcular total final
        double total = (totalPeople - 1) * priceAfterGroupDiscount
                - totalBirthdayDiscount
                + ownerPrice;


        // 7. Crear y guardar la reserva
        Booking booking = createBookingEntity(request, owner, participants, karts, pricing, basePrice, total);
        Booking savedBooking = bookingRepository.save(booking);

        // 8. Crear y guardar la factura
        Invoice invoice = createInvoice(savedBooking, owner, total);
        invoiceRepository.save(invoice);

        // 9. Asociar factura a la reserva
        savedBooking.setInvoice(invoice);
        return bookingRepository.save(savedBooking);
    }

    // --- Métodos auxiliares ---

    private Client validateOwner(Long ownerId) {
        return clientRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    private List<Client> validateParticipants(List<Long> participantIds) {
        List<Client> participants = clientRepository.findAllById(participantIds);
        if (participants.size() != participantIds.size()) {
            throw new RuntimeException("Algunos participantes no fueron encontrados");
        }
        return participants;
    }

    private List<Kart> validateKarts(List<String> kartCodes) {
        List<Kart> karts = kartRepository.findAllById(kartCodes);
        if (karts.size() != kartCodes.size()) {
            throw new RuntimeException("Algunos karts no están disponibles");
        }
        return karts;
    }

    private Pricing validatePricing(Integer laps, Integer duration) {
        return pricingRepository.findByLapsAndTotalDuration(laps, duration)
                .orElseThrow(() -> new RuntimeException("Tarifa no disponible para esta configuración"));
    }

    private List<Client> prepareParticipants(Client owner, List<Client> participants) {
        List<Client> allParticipants = new ArrayList<>(participants);
        allParticipants.add(owner);
        return allParticipants;
    }

    private void incrementVisits(Client owner, List<Client> participants) {
        owner.incrementMonthlyVisits();
        participants.forEach(Client::incrementMonthlyVisits);
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private boolean isHoliday(LocalDate date) {
        // Lista de feriados (ejemplo para Chile)
        Set<LocalDate> holidays = Set.of(
                LocalDate.of(date.getYear(), 1, 1),   // Año Nuevo
                LocalDate.of(date.getYear(), 5, 1),   // Día del Trabajo
                LocalDate.of(date.getYear(), 9, 18),  // Fiestas Patrias
                LocalDate.of(date.getYear(), 9, 19),  // Día de las Glorias del Ejército
                LocalDate.of(date.getYear(), 12, 25)  // Navidad
        );
        return holidays.contains(date);
    }

    private int calculateMaxBirthdayDiscounts(int groupSize) {
        if (groupSize >= 3 && groupSize <= 5) {
            return 1; // 1 descuento para grupos de 3-5
        } else if (groupSize >= 6 && groupSize <= 10) {
            return 2; // 2 descuentos para grupos de 6-10
        }
        return 0; // No aplica para otros tamaños
    }

    private long countBirthdayPeople(List<Client> participants) {
        return participants.stream()
                .filter(Client::isBirthdayToday)
                .count();
    }

    private double calculateFinalBasePrice(double basePrice, boolean isWeekend, boolean isHoliday) {
        double finalPrice = basePrice;
        if (isWeekend) {
            finalPrice *= (1 + WEEKEND_SURCHARGE);
        }
        if (isHoliday && !isWeekend) { // Solo aplicar si no es fin de semana
            finalPrice *= (1 + HOLIDAY_SURCHARGE);
        }
        return finalPrice;
    }

    private double calculateFinalTotalPrice(int totalPeople, double groupPrice, double ownerPrice, int birthdayDiscounts) {
        int regularParticipants = totalPeople - 1 - birthdayDiscounts;
        return (regularParticipants * groupPrice) + ownerPrice + (birthdayDiscounts * groupPrice * 0.5);
    }


    private Booking createBookingEntity(BookingRequest request, Client owner,
                                        List<Client> participants, List<Kart> karts,
                                        Pricing pricing, double basePrice, double total) {
        Booking booking = new Booking();
        booking.setReservationCode(generateReservationCode());
        booking.setDate(request.getDate());
        booking.setStartTime(request.getStartTime());
        booking.setDuration(request.getDuration());
        booking.setLaps(request.getLaps());
        booking.setStatus(Booking.Status.CONFIRMED);
        booking.setOwner(owner);
        booking.setParticipants(participants);
        booking.setAssignedKarts(karts);
        booking.setPricing(pricing);
        return booking;
    }

    private Invoice createInvoice(Booking booking, Client owner, double total) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setBooking(booking);
        invoice.setClientName(owner.getName());
        invoice.setClientEmail(owner.getEmail());
        invoice.setTotalToPay(total); // Considerar IVA si aplica
        invoice.setPdfFilePath("pendiente.pdf");
        return invoice;
    }

    private String generateReservationCode() {
        String code;
        do {
            code = "RES-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (bookingRepository.existsByReservationCode(code));
        return code;
    }

    private List<Kart> assignAvailableKarts(LocalDate date, LocalTime startTime, int duration, int kartsNeeded) {
        LocalTime endTime = startTime.plusMinutes(duration);
        List<Kart> availableKarts = kartRepository.findAvailableKarts(date, startTime, endTime);

        if (availableKarts.size() < kartsNeeded) {
            throw new IllegalStateException(
                    "No hay suficientes karts. Requeridos: " + kartsNeeded +
                            ", Disponibles: " + availableKarts.size()
            );
        }

        return availableKarts.stream()
                .limit(kartsNeeded)
                .collect(Collectors.toList());
    }

    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}