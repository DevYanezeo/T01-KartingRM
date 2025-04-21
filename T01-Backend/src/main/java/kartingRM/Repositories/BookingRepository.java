package kartingRM.Repositories;

import kartingRM.Entities.Booking;
import kartingRM.Entities.Booking.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByDateAndStartTime(LocalDate date, LocalTime startTime);

    @Query(
            value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
                    "FROM Booking b " +
                    "WHERE b.date = :date AND b.status <> 'CANCELLED' AND " +
                    "((b.start_time <= :startTime AND ADDTIME(b.start_time, SEC_TO_TIME(b.duration * 60)) > :startTime) OR " +
                    "(b.start_time < ADDTIME(:startTime, SEC_TO_TIME(:duration * 60)) AND " +
                    "ADDTIME(b.start_time, SEC_TO_TIME(b.duration * 60)) >= ADDTIME(:startTime, SEC_TO_TIME(:duration * 60))))",
            nativeQuery = true
    )
    boolean existsOverlappingBooking(LocalDate date, LocalTime startTime, int duration);


    List<Booking> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Booking> findByDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, Status status);
}