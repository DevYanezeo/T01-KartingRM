package kartingRM.Controllers;

import kartingRM.Entities.Discount;
import kartingRM.Services.DiscountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {
    @Autowired
    private DiscountServices discountService;

    @GetMapping
    public List<Discount> getAllDiscounts() {
        return discountService.findAllDiscounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Discount> getDiscountById(@PathVariable Long id) {
        try {
            Discount discount = discountService.findDiscountById(id);
            return ResponseEntity.ok(discount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Discount createDiscount(@RequestBody Discount discount) {
        return discountService.saveDiscount(discount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Discount> updateDiscount(@PathVariable Long id, @RequestBody Discount discountDetails) {
        try {
            Discount discount = discountService.findDiscountById(id);
            discount.setDiscountType(discountDetails.getDiscountType());
            discount.setPercentage(discountDetails.getPercentage());
            discount.setMinGroupSize(discountDetails.getMinGroupSize());
            discount.setMaxGroupSize(discountDetails.getMaxGroupSize());
            discount.setMinVisits(discountDetails.getMinVisits());
            discount.setMaxVisits(discountDetails.getMaxVisits());
            return ResponseEntity.ok(discountService.saveDiscount(discount));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/group-discount")
    public ResponseEntity<Double> getGroupDiscount(@RequestParam Integer groupSize) {
        return ResponseEntity.ok(discountService.getGroupDiscount(groupSize));
    }

    @GetMapping("/frequent-client-discount")
    public ResponseEntity<Double> getFrequentClientDiscount(@RequestParam Integer visits) {
        return ResponseEntity.ok(discountService.getFrequentClientDiscount(visits));
    }

    @GetMapping("/birthday-discount")
    public ResponseEntity<Double> getBirthdayDiscount() {
        return ResponseEntity.ok(discountService.getBirthdayDiscount());
    }
}