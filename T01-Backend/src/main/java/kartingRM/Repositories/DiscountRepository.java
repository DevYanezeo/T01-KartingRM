package kartingRM.Repositories;

import kartingRM.Entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Query("SELECT d FROM Discount d WHERE d.discountType = 'GROUP_SIZE' AND " +
            "d.minGroupSize <= ?1 AND (d.maxGroupSize IS NULL OR d.maxGroupSize >= ?1)")
    Optional<Discount> findApplicableGroupDiscount(Integer groupSize);

    @Query("SELECT d FROM Discount d WHERE d.discountType = 'FREQUENT_CLIENT' AND " +
            "d.minVisits <= ?1 AND (d.maxVisits IS NULL OR d.maxVisits >= ?1)")
    Optional<Discount> findApplicableFrequentClientDiscount(Integer visits);

    Optional<Discount> findByDiscountType(String discountType);
}