package kartingRM.Controllers;

import kartingRM.DTOs.BookingRequest;
import kartingRM.Entities.Booking;
import kartingRM.Services.BookingServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingServices bookingService;

    public BookingController(BookingServices bookingService) {
        this.bookingService = bookingService;
    }

    // ✅ Crear una nueva reserva
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request) {
        Booking createdBooking = bookingService.createBooking(request);
        return ResponseEntity.ok(createdBooking);
    }

}
