package kartingRM.Repositories;

import kartingRM.Entities.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PricingRepository extends JpaRepository<Pricing, Long> {
    // Buscar tarifa según cantidad de vueltas y duración total
    Optional<Pricing> findByLapsAndTotalDuration(Integer laps, Integer totalDuration);

    @Query("SELECT p.totalDuration FROM Pricing p WHERE p.laps = :laps")
    Optional<Integer> findDurationByLaps(@Param("laps") int laps);
}

