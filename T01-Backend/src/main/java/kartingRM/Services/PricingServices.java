package kartingRM.Services;

import kartingRM.Entities.Pricing;
import kartingRM.Repositories.PricingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PricingServices {
    private final PricingRepository pricingRepository;

    public List<Pricing> getAllPricings() {
        return pricingRepository.findAll();
    }

    public Pricing getPricingById(Long id) {
        return pricingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tarifa no encontrada con ID: " + id));
    }

    public Pricing getPricingByLapsAndDuration(int laps, int duration) {
        return pricingRepository.findByLapsAndTotalDuration(laps, duration)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada para esos valores"));
    }

    public int getDurationByLaps(int laps) {
        return pricingRepository.findDurationByLaps(laps)
                .orElseThrow(() -> new RuntimeException("No se encontró duración para esas vueltas"));
    }

    public Pricing savePricing(Pricing pricing) {
        return pricingRepository.save(pricing);
    }

    public Pricing updatePricing(Long id, Pricing pricingDetails) {
        Pricing pricing = getPricingById(id);
        pricing.setLaps(pricingDetails.getLaps());
        pricing.setBasePrice(pricingDetails.getBasePrice());
        pricing.setTotalDuration(pricingDetails.getTotalDuration());
        return pricingRepository.save(pricing);
    }

    public void deletePricing(Long id) {
        Pricing pricing = getPricingById(id);
        pricingRepository.delete(pricing);
    }
}