package kartingRM.Services;

import kartingRM.Entities.AppliedDiscount;
import kartingRM.Entities.Invoice;
import kartingRM.Entities.Client;
import kartingRM.Repositories.AppliedDiscountRepository;
import kartingRM.Repositories.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiscountApplicationServicesTest {

    @Mock
    private AppliedDiscountRepository appliedDiscountRepository;

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private ClientServices clientServices;

    @Mock
    private DiscountServices discountServices;

    @InjectMocks
    private DiscountApplicationServices discountApplicationServices;

    private Invoice invoice;
    private Client client;

    @BeforeEach
    public void setUp() {
        // Inicializamos los objetos que vamos a utilizar en las pruebas
        invoice = new Invoice();
        client = new Client();
        client.setMonthlyVisits(5);
    }

    @Test
    public void testApplyGroupDiscount() {
        // Configura los valores esperados
        int groupSize = 5;
        double subtotal = 100.0;

        // Simula el comportamiento del servicio DiscountServices
        when(discountServices.getApplicableGroupDiscount(groupSize)).thenReturn(0.1); // 10% de descuento

        // Llama al método
        AppliedDiscount appliedDiscount = discountApplicationServices.applyGroupDiscount(invoice, groupSize, subtotal);

        // Verifica el comportamiento esperado
        assertEquals("Descuento por grupo (5 personas)", appliedDiscount.getAppliedDescription());
        assertEquals(subtotal * 0.1, appliedDiscount.getDiscountAmount());
        assertNotNull(appliedDiscount.getAppliedDate());
    }

    @Test
    public void testApplyFrequentClientDiscount() {
        // Configura los valores esperados
        double clientSubtotal = 100.0;

        // Simula el comportamiento del servicio DiscountServices
        when(discountServices.getApplicableFrequentClientDiscount(client.getMonthlyVisits())).thenReturn(0.15); // 15% de descuento

        // Llama al método
        AppliedDiscount appliedDiscount = discountApplicationServices.applyFrequentClientDiscount(invoice, client, clientSubtotal);

        assertEquals("Descuento cliente frecuente (5 visitas)", appliedDiscount.getAppliedDescription());
        assertEquals(clientSubtotal * 0.15, appliedDiscount.getDiscountAmount());
        assertNotNull(appliedDiscount.getAppliedDate());
    }

    @Test
    public void testApplyBirthdayDiscount() {
        // Configura los valores esperados
        double clientSubtotal = 100.0;

        // Llama al método
        AppliedDiscount appliedDiscount = discountApplicationServices.applyBirthdayDiscount(invoice, client, clientSubtotal);

        // Verifica el comportamiento esperado
        assertEquals("Descuento por cumpleaños", appliedDiscount.getAppliedDescription());
        assertEquals(clientSubtotal * 0.5, appliedDiscount.getDiscountAmount()); // 50% descuento
        assertNotNull(appliedDiscount.getAppliedDate());
    }

    @Test
    public void testSaveAllAppliedDiscounts() {
        AppliedDiscount discount1 = new AppliedDiscount();
        AppliedDiscount discount2 = new AppliedDiscount();
        List<AppliedDiscount> discounts = Arrays.asList(discount1, discount2);

        when(appliedDiscountRepository.saveAll(discounts)).thenReturn(discounts);

        // Llama al método
        List<AppliedDiscount> savedDiscounts = discountApplicationServices.saveAllAppliedDiscounts(discounts);

        assertEquals(discounts, savedDiscounts);
        verify(appliedDiscountRepository, times(1)).saveAll(discounts);
    }
}
