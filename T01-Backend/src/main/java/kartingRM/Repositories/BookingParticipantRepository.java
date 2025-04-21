package kartingRM.Repositories;

import kartingRM.Entities.BookingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingParticipantRepository extends JpaRepository<BookingParticipant, Long> {
    List<BookingParticipant> findByBookingId(Long bookingId);

    @Modifying
    @Query("DELETE FROM BookingParticipant bp WHERE bp.booking.id = :bookingId")
    void deleteByBookingId(Long bookingId);
}