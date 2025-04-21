package kartingRM.Services;

import kartingRM.Entities.Pricing;
import kartingRM.Repositories.PricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PricingServices {
    @Autowired
    private PricingRepository pricingRepository;

    public List<Pricing> findAllPricings() {
        return pricingRepository.findAll();
    }

    public Pricing findPricingById(Long id) {
        return pricingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));
    }

    public Pricing findPricingByLaps(Integer laps) {
        return pricingRepository.findByLaps(laps)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada para " + laps + " vueltas"));
    }

    public Pricing savePricing(Pricing pricing) {
        return pricingRepository.save(pricing);
    }

    public void deletePricing(Long id) {
        pricingRepository.deleteById(id);
    }

    public double calculatePrice(Integer laps, boolean isWeekend, boolean isHoliday) {
        Pricing pricing = findPricingByLaps(laps);
        return pricing.getPriceForDay(isWeekend, isHoliday);
    }
}