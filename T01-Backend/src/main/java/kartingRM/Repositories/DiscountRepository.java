package kartingRM.Repositories;

import kartingRM.Entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    // Para descuentos por visitas
    @Query("SELECT d FROM Discount d WHERE " +
            "d.discountType = 'FREQUENT_CLIENT' AND " +
            "d.minVisits <= :visits AND " +
            "(d.maxVisits >= :visits OR d.maxVisits IS NULL)")
    Optional<Discount> findByTypeAndVisits(String type, int visits);

    // Para descuentos por grupo
    @Query("SELECT d FROM Discount d WHERE " +
            "d.discountType = 'GROUP_SIZE' AND " +
            "d.minGroupSize <= :groupSize AND " +
            "(d.maxGroupSize >= :groupSize OR d.maxGroupSize IS NULL)")
    Optional<Discount> findByTypeAndGroupSize(String type, int groupSize);

    // Para otros descuentos
    Optional<Discount> findByType(String type);
}