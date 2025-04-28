// Repositories/BookingRepository.java
package kartingRM.Repositories;

import kartingRM.Entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByReservationCode(String reservationCode);
    @Query("SELECT DISTINCT b FROM Booking b " +
            "LEFT JOIN FETCH b.owner " +
            "LEFT JOIN FETCH b.assignedKarts " +
            "LEFT JOIN FETCH b.pricing " +
            "WHERE b.date BETWEEN :startDate AND :endDate " +
            "ORDER BY b.date, b.startTime")
    List<Booking> findByDateBetweenWithDetails(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT DISTINCT b FROM Booking b " +
            "LEFT JOIN FETCH b.owner " +
            "LEFT JOIN FETCH b.assignedKarts k " +
            "LEFT JOIN FETCH b.pricing " +
            "WHERE b.date BETWEEN :startDate AND :endDate " +
            "AND k.kartCode = :kartCode " +
            "ORDER BY b.date, b.startTime")
    List<Booking> findByDateBetweenAndKart(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("kartCode") String kartCode);

    List<Booking> findByDate(LocalDate date);
}

