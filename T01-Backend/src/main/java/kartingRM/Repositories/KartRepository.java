package kartingRM.Repositories;

import kartingRM.Entities.Kart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface KartRepository extends JpaRepository<Kart, String> {
    Optional<Kart> findByKartCode(String kartCode);
    boolean existsByKartCode(String kartCode);

    // Karts disponibles (no en mantenimiento y estado DISPONIBLE)
    @Query("SELECT k FROM Kart k WHERE k.underMaintenance = false")
    List<Kart> findAvailableKartsOnlyByMaintenance();

    // Contar karts disponibles
    @Query("SELECT COUNT(k) FROM Kart k WHERE k.underMaintenance = false")
    long countAvailableKartsOnlyByMaintenance();

    @Query("SELECT k FROM Kart k " +
            "WHERE k.underMaintenance = false " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM Booking b JOIN b.assignedKarts ak " +
            "   WHERE ak.kartCode = k.kartCode " +
            "   AND b.date = :date " +
            "   AND (b.startTime < :endTime AND b.endTime > :startTime)" +
            ")")
    List<Kart> findAvailableKarts(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // Método anterior mejorado
    @Query("SELECT k FROM Kart k WHERE k.underMaintenance = false " +
            "AND NOT EXISTS (SELECT b FROM Booking b JOIN b.assignedKarts ak " +
            "WHERE ak.kartCode = k.kartCode AND b.date = :date " +
            "AND ((b.startTime < :endTime AND b.endTime > :startTime)))")
    List<Kart> findAvailableKartsForTimeSlot(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);


    // Método adicional para compatibilidad (si lo necesitas)
    @Query(value = "SELECT * FROM Kart k WHERE k.under_maintenance = false", nativeQuery = true)
    List<Kart> findAvailableKartsBasic();

    @Query("SELECT COUNT(b) FROM Booking b JOIN b.assignedKarts k WHERE k.kartCode = :kartCode")
    int countByKartCode(@Param("kartCode") String kartCode);


}