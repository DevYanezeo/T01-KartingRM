package kartingRM.Services;

import jakarta.transaction.Transactional;
import kartingRM.DTOs.BookingDTO;
import kartingRM.Entities.*;
import kartingRM.Repositories.*;
import kartingRM.DTOs.BookingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final PricingServices pricingServices;
    private final BusinessHourService businessHourService;

    // En BookingServices
    public Booking createBooking(BookingRequest request) {

        // 0. Validar que el horario de reserva está dentro del horario comercial
        if (!businessHourService.isWithinBusinessHours(request.getDate(), request.getStartTime(),
                request.getStartTime().plusMinutes(pricingServices.getDurationByLaps(request.getLaps())))) {
            throw new IllegalArgumentException("El horario de la reserva está fuera del horario comercial");
        }

        // 1. Validar clientes
        Client owner = clientServices.validateClientById(request.getOwnerId());
        List<Client> participants = clientServices.validateClientsByIds(request.getParticipantIds());
        List<Client> allParticipants = prepareParticipantsList(owner, participants);

        // 2. Calcular duración según vueltas
        int duration = pricingServices.getDurationByLaps(request.getLaps());

        // 3. Validar disponibilidad y asignar karts
        List<Kart> karts = kartServices.assignKartsForBooking(
                request.getDate(),
                request.getStartTime(),
                duration,
                allParticipants.size()
        );

        // 4. Incrementar visitas de clientes
        clientServices.incrementVisits(allParticipants);

        // 5. Obtener precio base y pricing
        Pricing pricing = pricingServices.getPricingByLapsAndDuration(request.getLaps(), duration);

        // 6. Calcular precio base total (sin descuentos aún)
        double baseTotal = pricing.getBasePrice() * (allParticipants.size());

        // 7. Crear y guardar la reserva
        Booking booking = createBookingEntity(request, owner, allParticipants, duration, karts, pricing);
        Booking savedBooking = bookingRepository.save(booking);

        // 8. Generar factura (que ahora calculará los descuentos)
        Invoice invoice = invoiceServices.generateAndSaveInvoice(savedBooking, baseTotal);

        // 9. Asociar factura a reserva y guardar
        savedBooking.setInvoice(invoice);
        return bookingRepository.save(savedBooking);
    }


    public List<Client> prepareParticipantsList(Client owner, List<Client> participants) {
        List<Client> allParticipants = new ArrayList<>(participants.stream()
                .distinct()
                .filter(p -> !p.equals(owner)) // Eliminar owner si está en participantes
                .collect(Collectors.toList()));
        allParticipants.add(owner);
        return allParticipants;
    }

    private Booking createBookingEntity(BookingRequest request, Client owner,
                                        List<Client> participants, int duration,
                                        List<Kart> karts, Pricing pricing) {
        Booking booking = new Booking();
        booking.setReservationCode(generateReservationCode());
        booking.setDate(request.getDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getStartTime().plusMinutes(duration));
        booking.setLaps(request.getLaps());
        booking.setDuration(duration);
        booking.setStatus(Booking.Status.CONFIRMED);
        booking.setOwner(owner);
        booking.setParticipants(participants);
        booking.setAssignedKarts(karts);
        booking.setPricing(pricing);
        return booking;
    }

    private String generateReservationCode() {
        String code;
        do {
            code = "RES-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (bookingRepository.existsByReservationCode(code));
        return code;
    }

    // Añade estos métodos a tu BookingServices
    public List<BookingDTO> getBookingsByDate(LocalDate date) {
        List<Booking> bookings = bookingRepository.findByDate(date);
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setReservationCode(booking.getReservationCode());
        dto.setDate(booking.getDate());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus().toString());
        dto.setOwnerName(booking.getOwner().getName());
        dto.setAssignedKarts(booking.getAssignedKarts().stream()
                .map(Kart::getKartCode)
                .collect(Collectors.toList()));
        dto.setLaps(booking.getLaps());
        dto.setDuration(booking.getDuration());
        return dto;
    }
}