package kartingRM.Repositories;

import kartingRM.Entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByReservationCode(String reservationCode);
}
