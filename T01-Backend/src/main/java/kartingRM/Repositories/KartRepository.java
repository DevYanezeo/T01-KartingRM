package kartingRM.Repositories;

import kartingRM.Entities.Kart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KartRepository extends JpaRepository<Kart, String> {
    Optional<Kart> findByKartCode(String kartCode);

    boolean existsByKartCode(String kartCode);

    /**
     * Busca karts disponibles (status=true) y no en mantenimiento
     */
    @Query("SELECT k FROM Kart k WHERE k.underMaintenance = true AND k.underMaintenance = false")
    List<Kart> findByStatusTrueAndUnderMaintenanceFalse();

    /**
     * Cuenta karts disponibles para reservas
     */
    @Query("SELECT COUNT(k) FROM Kart k WHERE k.underMaintenance = true AND k.underMaintenance = false")
    long countAvailableKarts();
}