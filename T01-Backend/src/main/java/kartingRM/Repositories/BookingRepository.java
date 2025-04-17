package kartingRM.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import kartingRM.Entities.Booking;


public interface BookingRepository extends JpaRepository<Booking, Integer> {
}
