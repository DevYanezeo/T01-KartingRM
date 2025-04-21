package kartingRM.Controllers;

import kartingRM.Entities.Pricing;
import kartingRM.Services.PricingServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pricings")
public class PricingController {
    @Autowired
    private PricingServices pricingServices;

    @GetMapping
    public List<Pricing> getAllPricings() {
        return pricingServices.findAllPricings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pricing> getPricingById(@PathVariable Long id) {
        try {
            Pricing pricing = pricingServices.findPricingById(id);
            return ResponseEntity.ok(pricing);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-laps/{laps}")
    public ResponseEntity<Pricing> getPricingByLaps(@PathVariable Integer laps) {
        try {
            Pricing pricing = pricingServices.findPricingByLaps(laps);
            return ResponseEntity.ok(pricing);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Pricing createPricing(@RequestBody Pricing pricing) {
        return pricingServices.savePricing(pricing);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pricing> updatePricing(@PathVariable Long id, @RequestBody Pricing pricingDetails) {
        try {
            Pricing pricing = pricingServices.findPricingById(id);
            pricing.setLaps(pricingDetails.getLaps());
            pricing.setMaxMinutes(pricingDetails.getMaxMinutes());
            pricing.setRegularPrice(pricingDetails.getRegularPrice());
            pricing.setWeekendPrice(pricingDetails.getWeekendPrice());
            pricing.setTotalDuration(pricingDetails.getTotalDuration());
            return ResponseEntity.ok(pricingServices.savePricing(pricing));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePricing(@PathVariable Long id) {
        pricingServices.deletePricing(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/calculate-price")
    public ResponseEntity<Double> calculatePrice(
            @RequestParam Integer laps,
            @RequestParam boolean isWeekend,
            @RequestParam boolean isHoliday) {
        try {
            double price = pricingServices.calculatePrice(laps, isWeekend, isHoliday);
            return ResponseEntity.ok(price);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}