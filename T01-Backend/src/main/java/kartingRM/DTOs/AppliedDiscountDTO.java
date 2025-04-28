package kartingRM.DTOs;

import kartingRM.Entities.AppliedDiscount;
import kartingRM.Entities.AppliedDiscount.DiscountType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppliedDiscountDTO {
    private Long id;
    private String appliedDescription;
    private double originalAmount;
    private double discountAmount;
    private LocalDateTime appliedDate;
    private DiscountType discountType;
    private Double discountPercentage;
    private boolean appliedToTotal;
    private String discountDescription;
    private String clientName;
    private String clientEmail;

    // Método estático para conversión desde la entidad
    public static AppliedDiscountDTO fromEntity(AppliedDiscount discount) {
        AppliedDiscountDTO dto = new AppliedDiscountDTO();
        dto.setId(discount.getId());
        dto.setAppliedDescription(discount.getAppliedDescription());
        dto.setOriginalAmount(discount.getOriginalAmount());
        dto.setDiscountAmount(discount.getDiscountAmount());
        dto.setAppliedDate(discount.getAppliedDate());
        dto.setDiscountType(discount.getDiscountType());
        dto.setDiscountPercentage(discount.getDiscountPercentage());
        dto.setAppliedToTotal(discount.isAppliedToTotal());

        // Descripción del tipo de descuento
        if (discount.getDiscountType() != null) {
            dto.setDiscountDescription(discount.getDiscountType().getDescription());
        }

        // Info del cliente si está disponible
        if (discount.getClient() != null) {
            dto.setClientName(discount.getClient().getName());
            dto.setClientEmail(discount.getClient().getEmail());
        }

        return dto;
    }
}