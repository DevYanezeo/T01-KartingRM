package kartingRM.Controllers;

import kartingRM.Entities.Pricing;
import kartingRM.Services.PricingServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingServices pricingServices;

    public PricingController(PricingServices pricingServices) {
        this.pricingServices = pricingServices;
    }

    // ✅ Obtener todas las tarifas
    @GetMapping
    public ResponseEntity<List<Pricing>> getAllPricings() {
        return ResponseEntity.ok(pricingServices.getAllPricings());
    }

    // ✅ Obtener una tarifa específica por vueltas y duración
    @GetMapping("/search")
    public ResponseEntity<Pricing> getPricingByLapsAndDuration(
            @RequestParam int laps,
            @RequestParam int duration) {
        return ResponseEntity.ok(pricingServices.getPricingByLapsAndDuration(laps, duration));
    }

    // ✅ Guardar nueva tarifa (si es necesario cargar precios)
    @PostMapping
    public ResponseEntity<Pricing> savePricing(@RequestBody Pricing pricing) {
        return ResponseEntity.ok(pricingServices.savePricing(pricing));
    }
}
