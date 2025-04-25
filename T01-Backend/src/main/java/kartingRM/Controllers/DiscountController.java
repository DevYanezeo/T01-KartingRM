package kartingRM.Controllers;

import kartingRM.Entities.Discount;
import kartingRM.Services.DiscountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountServices discountService;

    @Autowired
    public DiscountController(DiscountServices discountService) {
        this.discountService = discountService;
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
}