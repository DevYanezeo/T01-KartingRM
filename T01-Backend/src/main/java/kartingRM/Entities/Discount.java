package kartingRM.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String discountType; // GROUP_SIZE, FREQUENT_CLIENT, BIRTHDAY

    @Column(nullable = false)
    private Double percentage; // 0.10 para 10%, etc.

    // Para GROUP_SIZE (PDF pág.3)
    private Integer minGroupSize;
    private Integer maxGroupSize;

    // Para FREQUENT_CLIENT (PDF pág.3)
    private Integer minVisits;
    private Integer maxVisits;
}