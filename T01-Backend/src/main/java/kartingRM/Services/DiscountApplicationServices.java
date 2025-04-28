package kartingRM.Services;

import jakarta.transaction.Transactional;
import kartingRM.Entities.AppliedDiscount;
import kartingRM.Entities.Invoice;
import kartingRM.Entities.Client;
import kartingRM.Repositories.AppliedDiscountRepository;
import kartingRM.Repositories.DiscountRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountApplicationServices {
    private final AppliedDiscountRepository appliedDiscountRepository;
    private final DiscountRepository discountRepository;
    private final ClientServices clientServices;
    private final DiscountServices discountServices;

    public AppliedDiscount applyGroupDiscount(Invoice invoice, int groupSize, double subtotal) {
        Double discountPercentage = discountServices.getApplicableGroupDiscount(groupSize);

        return AppliedDiscount.builder()
                .invoice(invoice)
                .appliedDescription(String.format("Descuento por grupo (%d personas)", groupSize))
                .originalAmount(subtotal)
                .discountAmount(subtotal * discountPercentage)
                .appliedDate(LocalDateTime.now())
                .build();
    }

    public AppliedDiscount applyFrequentClientDiscount(Invoice invoice, Client client, double clientSubtotal) {
        Double discountPercentage = discountServices.getApplicableFrequentClientDiscount(client.getMonthlyVisits());

        return AppliedDiscount.builder()
                .invoice(invoice)
                .client(client)
                .appliedDescription(String.format("Descuento cliente frecuente (%d visitas)", client.getMonthlyVisits()))
                .originalAmount(clientSubtotal)
                .discountAmount(clientSubtotal * discountPercentage)
                .appliedDate(LocalDateTime.now())
                .build();
    }

    public AppliedDiscount applyBirthdayDiscount(Invoice invoice, Client client, double clientSubtotal) {
        return AppliedDiscount.builder()
                .invoice(invoice)
                .client(client)
                .appliedDescription("Descuento por cumpleaños")
                .originalAmount(clientSubtotal)
                .discountAmount(clientSubtotal * 0.5) // 50%
                .appliedDate(LocalDateTime.now())
                .build();
    }

    public List<AppliedDiscount> saveAllAppliedDiscounts(List<AppliedDiscount> discounts) {
        return appliedDiscountRepository.saveAll(discounts);
    }
}