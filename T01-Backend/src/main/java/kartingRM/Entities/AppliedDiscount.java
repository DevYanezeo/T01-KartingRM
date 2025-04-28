package kartingRM.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppliedDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount discount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    private String appliedDescription;
    private double originalAmount;
    private double discountAmount;
    private LocalDateTime appliedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    // Porcentaje aplicado (para referencia)
    private Double discountPercentage;

    @Column(nullable = false)
    private boolean isAppliedToTotal = false;

    // Método para calcular el monto final después del descuento
    public double getFinalAmount() {
        return originalAmount - discountAmount;
    }

    public enum DiscountType {
        BIRTHDAY("Descuento por cumpleaños"),
        FREQUENT_CLIENT("Descuento por cliente frecuente"),
        GROUP("Descuento por grupo"),
        SPECIAL_PROMOTION("Promoción especial"),
        OTHER("Otro descuento");

        private final String description;

        DiscountType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}