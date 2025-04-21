package kartingRM.Repositories;

import kartingRM.Entities.Kart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.LocalTime;

public interface KartRepository extends JpaRepository<Kart, String> {
    Optional<Kart> findByKartCode(String kartCode);

    boolean existsByKartCode(String kartCode);

    @Query("SELECT k FROM Kart k WHERE k.underMaintenance = false")
    List<Kart> findAvailableKartsOnlyByMaintenance();

    @Query("SELECT COUNT(k) FROM Kart k WHERE k.underMaintenance = false")
    long countAvailableKartsOnlyByMaintenance();

    @Query(
            value = "SELECT * FROM Kart k WHERE k.under_maintenance = false AND k.kart_code NOT IN (" +
                    "SELECT b.kart_code FROM bookings b WHERE b.date = :date AND b.status <> 'CANCELLED' AND " +
                    "((b.start_time <= :startTime AND ADDTIME(b.start_time, SEC_TO_TIME(b.duration * 60)) > :startTime) OR " +
                    "(b.start_time < ADDTIME(:startTime, SEC_TO_TIME(:duration * 60)) AND " +
                    "ADDTIME(b.start_time, SEC_TO_TIME(b.duration * 60)) >= ADDTIME(:startTime, SEC_TO_TIME(:duration * 60)))))",
            nativeQuery = true
    )
    List<Kart> findAvailableKarts(LocalDate date, LocalTime startTime, int duration);
}
