package kartingRM.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class PricingDTO {
    private Long id;
    private Integer laps;
    private Double basePrice;
    private Integer totalDuration;
}