package kartingRM.Services;

import kartingRM.Entities.Discount;
import kartingRM.Repositories.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DiscountServices {
    @Autowired
    private DiscountRepository discountRepository;

    public List<Discount> findAllDiscounts() {
        return discountRepository.findAll();
    }

    public Discount findDiscountById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Descuento no encontrado"));
    }

    public Discount saveDiscount(Discount discount) {
        return discountRepository.save(discount);
    }

    public void deleteDiscount(Long id) {
        discountRepository.deleteById(id);
    }

    public Double getGroupDiscount(Integer groupSize) {
        return discountRepository.findApplicableGroupDiscount(groupSize)
                .map(Discount::getPercentage)
                .orElse(0.0);
    }

    public Double getFrequentClientDiscount(Integer visits) {
        return discountRepository.findApplicableFrequentClientDiscount(visits)
                .map(Discount::getPercentage)
                .orElse(0.0);
    }

    public Double getBirthdayDiscount() {
        return discountRepository.findByDiscountType("BIRTHDAY")
                .map(Discount::getPercentage)
                .orElse(0.0);
    }
}