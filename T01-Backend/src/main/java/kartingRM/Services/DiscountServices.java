package kartingRM.Services;

import kartingRM.Entities.*;
import kartingRM.Repositories.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DiscountServices {
    private final DiscountRepository discountRepository;

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

    public Map<String, Double> calculateDiscountSummary(Client owner, List<Client> participants, double basePrice) {
        Map<String, Double> summary = new LinkedHashMap<>();
        int totalPeople = participants.size() + 1;

        // Descuento por grupo
        double groupDiscount = getApplicableGroupDiscount(totalPeople);
        if (groupDiscount > 0) {
            summary.put("Descuento por grupo", basePrice * totalPeople * groupDiscount);
        }

        // Descuento por cliente frecuente
        double frequentDiscount = getApplicableFrequentClientDiscount(owner.getMonthlyVisits());
        if (frequentDiscount > 0) {
            summary.put("Descuento cliente frecuente", basePrice * frequentDiscount);
        }

        // Descuento por cumpleaños
        int birthdayPeople = countBirthdayPeople(participants);
        int maxBirthdayDiscounts = calculateMaxBirthdayDiscounts(totalPeople);
        int applicableBirthdayDiscounts = Math.min(birthdayPeople, maxBirthdayDiscounts);

        if (applicableBirthdayDiscounts > 0) {
            double birthdayDiscount = 0.5; // 50%
            summary.put("Descuento cumpleaños", basePrice * applicableBirthdayDiscounts * birthdayDiscount);
        }

        return summary;
    }

    public double calculateTotalPriceWithDiscounts(Pricing pricing, Client owner, List<Client> participants) {
        // Asegurar que participants no incluya al owner
        List<Client> otherParticipants = participants.stream()
                .filter(p -> !p.equals(owner))
                .toList();

        int totalPeople = otherParticipants.size() + 1;
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

        // Calcular descuentos por cumpleaños
        long birthdayCount = countBirthdayPeople(otherParticipants);
        int maxBirthdayDiscounts = calculateMaxBirthdayDiscounts(totalPeople);
        long applicableBirthdayDiscounts = Math.min(birthdayCount, maxBirthdayDiscounts);

        // Calcular precio total
        double participantsPriceNoDiscount = priceAfterGroupDiscount * otherParticipants.size();
        double birthdayDiscount = priceAfterGroupDiscount * 0.5 * applicableBirthdayDiscounts;

        return ownerPrice + participantsPriceNoDiscount - birthdayDiscount;
    }

    private int countBirthdayPeople(List<Client> clients) {
        return (int) clients.stream()
                .filter(Client::isBirthdayToday)
                .count();
    }

    private int calculateMaxBirthdayDiscounts(int groupSize) {
        if (groupSize >= 3 && groupSize <= 5) {
            return 1; // 1 descuento para grupos de 3-5
        } else if (groupSize >= 6 && groupSize <= 10) {
            return 2; // 2 descuentos para grupos de 6-10
        }
        return 0; // No aplica para otros tamaños
    }

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    public Discount getDiscountById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Descuento no encontrado con ID: " + id));
    }

    public Discount createDiscount(Discount discount) {
        return discountRepository.save(discount);
    }

    public Discount updateDiscount(Long id, Discount discountDetails) {
        Discount discount = getDiscountById(id);
        discount.setDiscountType(discountDetails.getDiscountType());
        discount.setPercentage(discountDetails.getPercentage());
        discount.setMinGroupSize(discountDetails.getMinGroupSize());
        discount.setMaxGroupSize(discountDetails.getMaxGroupSize());
        discount.setMinVisits(discountDetails.getMinVisits());
        discount.setMaxVisits(discountDetails.getMaxVisits());
        return discountRepository.save(discount);
    }

    public void deleteDiscount(Long id) {
        Discount discount = getDiscountById(id);
        discountRepository.delete(discount);
    }


}