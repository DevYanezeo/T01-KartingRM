package kartingRM.Controllers;

import kartingRM.Entities.Discount;
import kartingRM.Services.DiscountServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@CrossOrigin(origins = "*")

public class DiscountController {

    private final DiscountServices discountService;

    @Autowired
    public DiscountController(DiscountServices discountService) {
        this.discountService = discountService;
    }

    // Nuevo: Obtener todos los descuentos
    @GetMapping
    public ResponseEntity<List<Discount>> getAllDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    // Endpoint para descuento por tamaño de grupo
    @GetMapping("/group-discount")
    public ResponseEntity<Double> getGroupDiscount(@RequestParam Integer groupSize) {
        Double discount = discountService.getApplicableGroupDiscount(groupSize);
        return ResponseEntity.ok(discount);
    }

    // Endpoint para descuento por cliente frecuente
    @GetMapping("/frequent-client-discount")
    public ResponseEntity<Double> getFrequentClientDiscount(@RequestParam Integer visits) {
        Double discount = discountService.getApplicableFrequentClientDiscount(visits);
        return ResponseEntity.ok(discount);
    }

    // Endpoint para descuento por cumpleaños
    @GetMapping("/birthday-discount")
    public ResponseEntity<Double> getBirthdayDiscount() {
        Double discount = discountService.getBirthdayDiscount();
        return ResponseEntity.ok(discount);
    }

    // Nuevo: Crear nuevo descuento
    @PostMapping
    public ResponseEntity<Discount> createDiscount(@RequestBody Discount discount) {
        return ResponseEntity.ok(discountService.createDiscount(discount));
    }

    // Nuevo: Actualizar descuento
    @PutMapping("/{id}")
    public ResponseEntity<Discount> updateDiscount(
            @PathVariable Long id,
            @RequestBody Discount discount) {
        return ResponseEntity.ok(discountService.updateDiscount(id, discount));
    }

    // Nuevo: Eliminar descuento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }

    // Nuevo: Obtener descuento por ID
    @GetMapping("/{id}")
    public ResponseEntity<Discount> getDiscountById(@PathVariable Long id) {
        return ResponseEntity.ok(discountService.getDiscountById(id));
    }
}