package kartingRM.Repositories;

import kartingRM.Entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    // 1. Descuento por tamaño de grupo (con LIMIT 1 para evitar múltiples resultados)
    @Query("SELECT d FROM Discount d WHERE d.discountType = 'GROUP_SIZE' " +
            "AND :groupSize BETWEEN d.minGroupSize AND d.maxGroupSize " +
            "ORDER BY d.percentage DESC")
    List<Discount> findGroupSizeDiscounts(@Param("groupSize") int groupSize);


    // 2. Descuento por cliente frecuente (optimizado)
    @Query("SELECT d FROM Discount d WHERE d.discountType = 'FREQUENT_CLIENT' " +
            "AND (:visits BETWEEN d.minVisits AND COALESCE(d.maxVisits, 2147483647)) " + // COALESCE maneja NULL como valor máximo
            "ORDER BY d.percentage DESC")
    List<Discount> findFrequentClientDiscounts(@Param("visits") int visits);

    // 3. Descuento por cumpleaños (con validación de existencia)
    @Query("SELECT d FROM Discount d WHERE d.discountType = 'BIRTHDAY'")
    Optional<Discount> findBirthdayDiscount();
}