package kartingRM.Services;

import jakarta.transaction.Transactional;
import kartingRM.Entities.*;
import kartingRM.Repositories.*;
import kartingRM.DTOs.BookingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServices {
    private final BookingRepository bookingRepository;
    private final ClientServices clientServices;
    private final DiscountServices discountServices;
    private final InvoiceServices invoiceServices;
    private final KartServices kartServices;
    private final PDFGeneratorServices pdfGenerator;
    private final PricingServices pricingServices;

    private static final double WEEKEND_SURCHARGE = 0.05; // +5%
    private static final double HOLIDAY_SURCHARGE = 0.05; // +5%

    public Booking createBooking(BookingRequest request) {
        // 1. Validar datos básicos y calcular duración
        Client owner = validateOwner(request.getOwnerId());
        List<Client> participants = validateParticipants(request.getParticipantIds());

        int duration = pricingServices.getDurationByLaps(request.getLaps());

        // 2. Validar disponibilidad de karts (con la duración calculada)
        List<Kart> karts = assignAvailableKarts(
                request.getDate(),
                request.getStartTime(),
                duration,
                participants.size() + 1
        );

        List<Client> allParticipants = new ArrayList<>(participants);
        allParticipants.add(owner);
        int totalPeople = participants.size();

        // 3. Incrementar visitas de clientes
        incrementVisits(owner, participants);

        // 3. Obtener Pricing (con precio base) usando ambos valores
        Pricing pricing = pricingServices.getPricingByLapsAndDuration(
                request.getLaps(),
                duration
        );
        double basePrice = pricing.getBasePrice();
        Map<String, Double> discountSummary = calculateDiscountSummary(owner, participants, pricing.getBasePrice());
        double total = discountServices.calculateTotalPriceWithDiscounts(pricing, owner, participants);


        // 7. Crear y guardar la reserva
        Booking booking = createBookingEntity(request, owner, allParticipants, duration, karts, pricing, basePrice, total);
        Booking savedBooking = bookingRepository.save(booking);

        // Crear factura CON PDF
        Invoice invoice = invoiceServices.generateAndSaveInvoice(savedBooking, total, discountSummary);

        // Asociar factura a reserva
        savedBooking.setInvoice(invoice);
        return bookingRepository.save(savedBooking);


    }
    // --- Métodos auxiliares ---
    private Map<String, Double> calculateDiscountSummary(Client owner, List<Client> participants, double basePrice) {
        Map<String, Double> summary = new LinkedHashMap<>();

        // Descuento por grupo
        double groupDiscount = discountServices.getApplicableGroupDiscount(participants.size() + 1);
        if (groupDiscount > 0) {
            summary.put("Descuento por grupo", basePrice * (participants.size() + 1) * groupDiscount);
        }

        // Descuento por cliente frecuente
        double frequentDiscount = discountServices.getApplicableFrequentClientDiscount(owner.getMonthlyVisits());
        if (frequentDiscount > 0) {
            summary.put("Descuento cliente frecuente", basePrice * frequentDiscount);
        }

        // Descuento por cumpleaños
        long birthdayPeople = participants.stream().filter(Client::isBirthdayToday).count();
        if (birthdayPeople > 0) {
            double birthdayDiscount = discountServices.getBirthdayDiscount();
            summary.put("Descuento cumpleaños", basePrice * birthdayPeople * birthdayDiscount);
        }

        return summary;
    }

    private Client validateOwner(Long ownerId) {
        return clientServices.validateClientById(ownerId);
    }

    private List<Client> validateParticipants(List<Long> participantIds) {
        return clientServices.validateClientsByIds(participantIds);
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
                                        List<Client> participants, int duration, List<Kart> karts,
                                        Pricing pricing, double basePrice, double total) {
        Booking booking = new Booking();
        booking.setReservationCode(generateReservationCode());
        booking.setDate(request.getDate());
        booking.setStartTime(request.getStartTime());
        booking.setLaps(request.getLaps());
        booking.setDuration(duration);
        booking.setStatus(Booking.Status.CONFIRMED);
        booking.setOwner(owner);
        booking.setParticipants(participants);
        booking.setAssignedKarts(karts);
        booking.setPricing(pricing);
        return booking;
    }

    private Invoice createInvoice(Booking booking, Client owner, double total, Map<String, Double> discountSummary) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setBooking(booking);
        invoice.setClientName(owner.getName());
        invoice.setClientEmail(owner.getEmail());
        invoice.setTotalToPay(total);

        try {
            // Generar PDF con el resumen de descuentos
            byte[] pdfBytes = pdfGenerator.generateBasicInvoice(booking, invoice, discountSummary);
            invoice.setPdfData(pdfBytes);
            invoice.setPdfGenerated(true);
            invoice.setPdfFilePath("invoices/" + invoice.getInvoiceNumber() + ".pdf");
        } catch (IOException e) {
            // Manejar el error sin romper el flujo
            invoice.setPdfGenerated(false);
        }

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
        List<Kart> availableKarts = kartServices.getAvailableKartsForBooking(date, startTime, duration);

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