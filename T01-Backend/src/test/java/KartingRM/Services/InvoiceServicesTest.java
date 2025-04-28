package KartingRM.Services;

import kartingRM.Entities.*;
import kartingRM.Repositories.AppliedDiscountRepository;
import kartingRM.Repositories.InvoiceRepository;
import kartingRM.Config.TaxConfiguration;
import kartingRM.Services.InvoiceServices;
import kartingRM.Services.PDFGeneratorServices;
import kartingRM.Services.DiscountServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InvoiceServicesTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private AppliedDiscountRepository appliedDiscountRepository;

    @Mock
    private PDFGeneratorServices pdfGenerator;

    @Mock
    private TaxConfiguration taxConfiguration;

    @Mock
    private DiscountServices discountServices;

    private InvoiceServices invoiceServices;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        invoiceServices = new InvoiceServices(invoiceRepository, appliedDiscountRepository, pdfGenerator, taxConfiguration, discountServices);
    }

    @Test
    public void testGenerateAndSaveInvoice() throws IOException {
        // Prepare mocks
        Booking booking = mock(Booking.class);
        Client owner = mock(Client.class);
        Pricing pricing = mock(Pricing.class);
        AppliedDiscount discount = mock(AppliedDiscount.class);

        when(booking.getOwner()).thenReturn(owner);
        when(booking.getPricing()).thenReturn(pricing);
        when(owner.getName()).thenReturn("John Doe");
        when(owner.getEmail()).thenReturn("john.doe@example.com");
        when(pricing.getBasePrice()).thenReturn(100.0);
        when(discountServices.getApplicableGroupDiscount(anyInt())).thenReturn(0.1); // 10% for group discount

        // Mock Discount Application
        when(discountServices.getApplicableFrequentClientDiscount(anyInt())).thenReturn(0.05); // 5% frequent discount

        // Mock Applied Discount Repository
        when(appliedDiscountRepository.saveAll(anyList())).thenReturn(Collections.singletonList(discount));

        // Mock Tax Configuration
        when(taxConfiguration.calculateIva(anyDouble())).thenReturn(19.0); // Sample tax rate

        // Mock PDF Generation
        byte[] mockPdf = new byte[0];
        when(pdfGenerator.generateBasicInvoice(any(), any())).thenReturn(mockPdf);

        // Call service method
        Invoice result = invoiceServices.generateAndSaveInvoice(booking, 100.0);

        // Verifications
        assertNotNull(result);
        assertNotNull(result.getInvoiceNumber());
        assertEquals("John Doe", result.getClientName());
        assertEquals(100.0, result.getBaseRate());
        assertTrue(result.isPdfGenerated());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    public void testApplyDiscountsAndCalculateTotals() {
        // Prepare mocks for booking, pricing, and discount calculations
        Booking booking = mock(Booking.class);
        Pricing pricing = mock(Pricing.class);
        Client owner = mock(Client.class);
        AppliedDiscount discount = mock(AppliedDiscount.class);

        when(booking.getOwner()).thenReturn(owner);
        when(booking.getPricing()).thenReturn(pricing);
        when(owner.getName()).thenReturn("Jane Doe");
        when(owner.getEmail()).thenReturn("jane.doe@example.com");
        when(pricing.getBasePrice()).thenReturn(200.0);

        // Simulate frequent client discount
        when(discountServices.getApplicableFrequentClientDiscount(2)).thenReturn(0.05); // 5% discount

        // Mock Tax Configuration
        when(taxConfiguration.calculateIva(anyDouble())).thenReturn(38.0);

        // Mock group discount
        when(discountServices.getApplicableGroupDiscount(anyInt())).thenReturn(0.1); // 10% discount

        // Call service method
        Invoice invoice = new Invoice();
        invoice.setBaseRate(200.0);
        invoiceServices.applyDiscountsAndCalculateTotals(invoice, booking);

        // Assert final calculations
        assertEquals(171.0, invoice.getSubtotal()); // After discounts
        assertEquals(38.0, invoice.getIva()); // After tax
        assertEquals(209.0, invoice.getTotalToPay()); // Final total
    }

    @Test
    public void testGenerateInvoiceNumber() {
        String invoiceNumber = invoiceServices.generateInvoiceNumber();
        assertNotNull(invoiceNumber);
        assertTrue(invoiceNumber.startsWith("INV-"));
        assertEquals(9, invoiceNumber.length()); // "INV-" + 6 random characters
    }
}
