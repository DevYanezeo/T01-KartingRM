package kartingRM.Repositories;

import kartingRM.Entities.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PricingRepository extends JpaRepository<Pricing, Long> {
    // Buscar tarifa según cantidad de vueltas y duración total
    Optional<Pricing> findByLapsAndTotalDuration(Integer laps, Integer totalDuration);
}
