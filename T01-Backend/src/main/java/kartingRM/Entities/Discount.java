package kartingRM.Entities;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String discountType;  // "GROUP_SIZE", "FREQUENT_CLIENT", "BIRTHDAY", "SPECIAL_PROMO"

    private String description;

    @Column(nullable = false)
    private Double percentage;

    private Integer minGroupSize;
    private Integer maxGroupSize;

    private Integer minVisits;
    private Integer maxVisits;
}