package kartingRM.Services;

import kartingRM.Entities.Pricing;
import kartingRM.Repositories.PricingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingServices {

    private final PricingRepository pricingRepository;

    public PricingServices(PricingRepository pricingRepository) {
        this.pricingRepository = pricingRepository;
    }

    // Obtener todos los precios disponibles
    public List<Pricing> getAllPricings() {
        return pricingRepository.findAll();
    }

    // Buscar tarifa por duración y vueltas
    public Pricing getPricingByLapsAndDuration(int laps, int duration) {
        return pricingRepository.findByLapsAndTotalDuration(laps, duration)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada para esos valores"));
    }

    public int getDurationByLaps(int laps) {
        return pricingRepository.findDurationByLaps(laps)
                .orElseThrow(() -> new RuntimeException("No se encontró duración para esas vueltas"));
    }

    // Guardar nueva tarifa (opcional)
    public Pricing savePricing(Pricing pricing) {
        return pricingRepository.save(pricing);
    }
}
