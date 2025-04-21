package kartingRM.Controllers;

import kartingRM.Entities.Booking;
import kartingRM.Entities.Invoice;
import kartingRM.Services.BookingServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingServices bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestBody Booking booking,
            @RequestParam List<Long> participantIds) {
        try {
            Booking createdBooking = bookingService.createBooking(booking, participantIds);
            return ResponseEntity.ok(createdBooking);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/weekly-schedule")
    public List<Booking> getWeeklySchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return bookingService.getWeeklySchedule(startDate);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
        try {
            Booking booking = bookingService.cancelBooking(id);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Booking> completeBooking(@PathVariable Long id) {
        try {
            Booking booking = bookingService.completeBooking(id);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<Invoice> getBookingInvoice(@PathVariable Long id) {
        Optional<Invoice> invoice = bookingService.getBookingInvoice(id);
        return invoice.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}