package kartingRM.Controllers;

import kartingRM.Entities.Pricing;
import kartingRM.DTOs.PricingDTO;
import kartingRM.Services.PricingServices;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pricing")
@CrossOrigin(origins = "*")
public class PricingController {

    private final PricingServices pricingServices;

    public PricingController(PricingServices pricingServices) {
        this.pricingServices = pricingServices;
    }

    @GetMapping
    public ResponseEntity<List<PricingDTO>> getAllPricings() {
        List<Pricing> pricings = pricingServices.getAllPricings();
        List<PricingDTO> dtos = pricings.stream()
                .map(p -> new PricingDTO(p.getId(), p.getLaps(), p.getBasePrice(), p.getTotalDuration()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<Pricing> getPricingByLapsAndDuration(
            @RequestParam int laps,
            @RequestParam int duration) {
        return ResponseEntity.ok(pricingServices.getPricingByLapsAndDuration(laps, duration));
    }

    @PostMapping
    public ResponseEntity<Pricing> savePricing(@RequestBody Pricing pricing) {
        return ResponseEntity.ok(pricingServices.savePricing(pricing));
    }

    // Nuevo: Actualizar tarifa existente
    @PutMapping("/{id}")
    public ResponseEntity<Pricing> updatePricing(
            @PathVariable Long id,
            @RequestBody Pricing pricing) {
        return ResponseEntity.ok(pricingServices.updatePricing(id, pricing));
    }

    // Nuevo: Eliminar tarifa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePricing(@PathVariable Long id) {
        pricingServices.deletePricing(id);
        return ResponseEntity.noContent().build();
    }

    // Nuevo: Obtener tarifa por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pricing> getPricingById(@PathVariable Long id) {
        return ResponseEntity.ok(pricingServices.getPricingById(id));
    }
}