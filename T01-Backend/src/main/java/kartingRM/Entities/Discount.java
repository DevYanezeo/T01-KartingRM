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
    private String discountType;

    @Column(nullable = false)
    private Double percentage;

    private Integer minGroupSize;
    private Integer maxGroupSize;

    private Integer minVisits;
    private Integer maxVisits;
}