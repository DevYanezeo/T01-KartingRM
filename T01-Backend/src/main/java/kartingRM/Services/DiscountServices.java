package kartingRM.Services;

import kartingRM.Entities.*;
import kartingRM.Repositories.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiscountServices {
    @Autowired
    private DiscountRepository discountRepository;

    public Double getApplicableGroupDiscount(Integer groupSize) {
        List<Discount> discounts = discountRepository.findGroupSizeDiscounts(groupSize);
        return discounts.isEmpty() ? 0.0 : discounts.getFirst().getPercentage();
    }


    public Double getApplicableFrequentClientDiscount(Integer visits) {
        List<Discount> discounts = discountRepository.findFrequentClientDiscounts(visits);
        return discounts.isEmpty() ? 0.0 : discounts.getFirst().getPercentage();
    }

    public Double getBirthdayDiscount() {
        return discountRepository.findBirthdayDiscount()
                .map(Discount::getPercentage)
                .orElse(0.0);
    }

    public double calculateTotalPriceWithDiscounts(Pricing pricing, Client owner, List<Client> allParticipants) {
        // Asegurar que allParticipants siempre incluya al owner
        if (allParticipants == null || !allParticipants.contains(owner)) {
            allParticipants = new ArrayList<>();
            allParticipants.add(owner);
        }

        int totalPeople = allParticipants.size();
        double basePrice = pricing.getBasePrice();

        // Precio base con descuento de grupo
        double groupDiscount = getApplicableGroupDiscount(totalPeople);
        double priceAfterGroupDiscount = basePrice * (1 - groupDiscount);

        // Precio final owner (con descuento frecuente)
        double frequentDiscount = getApplicableFrequentClientDiscount(owner.getMonthlyVisits());
        double ownerPrice = priceAfterGroupDiscount * (1 - frequentDiscount);

        // Solo 1 persona (owner)
        if (totalPeople == 1) {
            return ownerPrice;
        }

        // Para grupos:
        long birthdayCount = allParticipants.stream()
                .filter(p -> !p.equals(owner)) // Excluir owner
                .filter(Client::isBirthdayToday)
                .count();

        return ownerPrice +
                (priceAfterGroupDiscount * (totalPeople - 1)) -
                (birthdayCount * priceAfterGroupDiscount * 0.5);
    }

}