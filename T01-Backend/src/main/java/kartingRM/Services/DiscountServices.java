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
}