package kartingRM.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TaxConfiguration {

    @Value("${application.tax.iva-percentage:0.19}")
    private double ivaPercentage;

    public double getIvaPercentage() {
        return ivaPercentage;
    }

    public double calculateIva(double subtotal) {
        return subtotal * ivaPercentage;
    }

    public double calculateTotalWithIva(double subtotal) {
        return subtotal + calculateIva(subtotal);
    }
}
