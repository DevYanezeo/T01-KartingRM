package kartingRM.Repositories;

import kartingRM.Entities.Discount;
import kartingRM.Entities.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PricingRepository extends JpaRepository<Pricing, Long> {
    Optional<Pricing> findByLaps(Integer laps);
    Optional<Pricing> findByMaxMinutes(Integer minutes);
}

