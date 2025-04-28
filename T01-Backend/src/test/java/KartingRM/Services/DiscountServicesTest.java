package KartingRM.Services;

import kartingRM.Entities.*;
import kartingRM.Repositories.DiscountRepository;
import kartingRM.Services.DiscountServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiscountServicesTest {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountServices discountServices;

    private Client owner;
    private Client participant;
    private Pricing pricing;

    @BeforeEach
    public void setUp() {
        // Inicializamos los objetos que vamos a utilizar en las pruebas
        owner = new Client();  // Cliente dueño
        owner.setMonthlyVisits(5);  // Por ejemplo, cliente frecuente con 5 visitas
        participant = new Client();  // Participante
        participant.setMonthlyVisits(3);  // Participante con 3 visitas
        pricing = new Pricing();  // Inicializamos un precio base
        pricing.setBasePrice(100.0); // Precio base
    }

    @Test
    public void testGetApplicableGroupDiscount() {
        // Simula los descuentos para grupos
        Discount discount = new Discount();
        discount.setPercentage(0.1);  // 10% de descuento
        List<Discount> discounts = Collections.singletonList(discount);
        when(discountRepository.findGroupSizeDiscounts(5)).thenReturn(discounts);

        Double result = discountServices.getApplicableGroupDiscount(5);

        // Verifica el resultado
        assertEquals(0.1, result);
    }

    @Test
    public void testGetApplicableFrequentClientDiscount() {
        // Simula los descuentos para clientes frecuentes
        Discount discount = new Discount();
        discount.setPercentage(0.15);  // 15% de descuento
        List<Discount> discounts = Collections.singletonList(discount);
        when(discountRepository.findFrequentClientDiscounts(5)).thenReturn(discounts);

        Double result = discountServices.getApplicableFrequentClientDiscount(5);

        // Verifica el resultado
        assertEquals(0.15, result);
    }

    @Test
    public void testGetBirthdayDiscount() {
        // Simula el descuento de cumpleaños
        Discount discount = new Discount();
        discount.setPercentage(0.5);  // 50% de descuento
        Optional<Discount> optionalDiscount = Optional.of(discount);
        when(discountRepository.findBirthdayDiscount()).thenReturn(optionalDiscount);

        Double result = discountServices.getBirthdayDiscount();

        // Verifica el resultado
        assertEquals(0.5, result);
    }

    @Test
    public void testCalculateDiscountSummary() {
        // Configura el precio base
        double basePrice = 100.0;

        // Simula descuentos aplicables
        when(discountRepository.findGroupSizeDiscounts(3)).thenReturn(Collections.singletonList(new Discount()));
        when(discountRepository.findFrequentClientDiscounts(5)).thenReturn(Collections.singletonList(new Discount()));

        Map<String, Double> summary = discountServices.calculateDiscountSummary(owner, Collections.singletonList(participant), basePrice);

        // Verifica el resumen
        assertTrue(summary.containsKey("Descuento por grupo"));
        assertTrue(summary.containsKey("Descuento cliente frecuente"));
    }

    @Test
    public void testCalculateTotalPriceWithDiscounts() {
        double totalPrice = discountServices.calculateTotalPriceWithDiscounts(pricing, owner, Collections.singletonList(participant));

        // Verifica el resultado esperado (por ejemplo, validando que el precio total no sea cero)
        assertTrue(totalPrice > 0);
    }

    @Test
    public void testCreateDiscount() {
        // Configura un descuento
        Discount discount = new Discount();
        discount.setPercentage(0.2);  // 20% de descuento

        // Simula el comportamiento del repositorio
        when(discountRepository.save(discount)).thenReturn(discount);

        Discount createdDiscount = discountServices.createDiscount(discount);

        // Verifica que el descuento se haya creado correctamente
        assertEquals(discount, createdDiscount);
    }

    @Test
    public void testUpdateDiscount() {
        // Configura el descuento original y el descuento actualizado
        Discount discount = new Discount();
        discount.setId(1L);
        discount.setPercentage(0.2);  // 20% de descuento

        Discount updatedDiscount = new Discount();
        updatedDiscount.setPercentage(0.3);  // 30% de descuento

        // Simula el comportamiento del repositorio
        when(discountRepository.findById(1L)).thenReturn(Optional.of(discount));
        when(discountRepository.save(updatedDiscount)).thenReturn(updatedDiscount);

        Discount result = discountServices.updateDiscount(1L, updatedDiscount);

        // Verifica que el descuento se haya actualizado correctamente
        assertEquals(updatedDiscount.getPercentage(), result.getPercentage());
    }

    @Test
    public void testDeleteDiscount() {
        // Configura un descuento
        Discount discount = new Discount();
        discount.setId(1L);

        // Simula el comportamiento del repositorio
        when(discountRepository.findById(1L)).thenReturn(Optional.of(discount));

        // Llama al método
        discountServices.deleteDiscount(1L);

        // Verifica que el repositorio haya sido llamado para eliminar el descuento
        verify(discountRepository, times(1)).delete(discount);
    }
}
