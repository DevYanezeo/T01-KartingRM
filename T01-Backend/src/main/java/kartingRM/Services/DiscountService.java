package kartingRM.Services;

import kartingRM.Entities.Discount;
import kartingRM.Entities.Client;
import kartingRM.Repositories.DiscountRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountService {
    @Autowired
    private DiscountRepository discountRepository;

    public DiscountCalculation calculateClientDiscounts(Client client, int groupSize) {
        DiscountCalculation calculation = new DiscountCalculation();

        // 1. Descuento por cliente frecuente (basado en monthlyVisits)
        calculation.setFrequentClientDiscount(
                discountRepository.findByTypeAndVisits("FREQUENT_CLIENT", client.getMonthlyVisits())
                        .map(Discount::getPercentage)
                        .orElse(0.0)
        );

        // 2. Descuento por cumpleaños
        if (client.isBirthdayToday()) {
            calculation.setBirthdayDiscount(
                    discountRepository.findByType("BIRTHDAY")
                            .map(Discount::getPercentage)
                            .orElse(0.0)
            );
        }

        // 3. Descuento por tamaño de grupo (no depende del cliente)
        calculation.setGroupDiscount(
                discountRepository.findByTypeAndGroupSize("GROUP_SIZE", groupSize)
                        .map(Discount::getPercentage)
                        .orElse(0.0)
        );

        return calculation;
    }
}

// Clase DTO para el resultado
@Data
class DiscountCalculation {
    private double frequentClientDiscount;
    private double birthdayDiscount;
    private double groupDiscount;

    public double getTotalDiscount() {
        // Lógica para combinar descuentos (evitar sumar más del 100%)
        return Math.min(0.8, frequentClientDiscount + birthdayDiscount + groupDiscount);
    }
}