package KartingRM.Services;

import kartingRM.Entities.Pricing;
import kartingRM.Repositories.PricingRepository;
import kartingRM.Services.PricingServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PricingServicesTest {

    @Mock
    private PricingRepository pricingRepository;

    private PricingServices pricingServices;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        pricingServices = new PricingServices(pricingRepository);
    }

    @Test
    public void testGetAllPricings() {
        Pricing pricing = new Pricing();
        pricing.setLaps(5);
        pricing.setBasePrice(1000.0);
        pricing.setTotalDuration(30);

        when(pricingRepository.findAll()).thenReturn(List.of(pricing));

        List<Pricing> pricingList = pricingServices.getAllPricings();

        assertNotNull(pricingList);
        assertEquals(1, pricingList.size());
        assertEquals(5, pricingList.get(0).getLaps());
        verify(pricingRepository).findAll();
    }

    @Test
    public void testGetPricingById() {
        Long pricingId = 1L;
        Pricing pricing = new Pricing();
        pricing.setLaps(5);
        pricing.setBasePrice(1000.0);
        pricing.setTotalDuration(30);

        when(pricingRepository.findById(pricingId)).thenReturn(Optional.of(pricing));

        Pricing foundPricing = pricingServices.getPricingById(pricingId);

        assertNotNull(foundPricing);
        assertEquals(pricingId, foundPricing.getId());
        assertEquals(5, foundPricing.getLaps());
        verify(pricingRepository).findById(pricingId);
    }

    @Test
    public void testGetPricingByIdNotFound() {
        Long pricingId = 1L;

        when(pricingRepository.findById(pricingId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            pricingServices.getPricingById(pricingId);
        });

        assertEquals("Tarifa no encontrada con ID: " + pricingId, exception.getMessage());
    }

    @Test
    public void testGetPricingByLapsAndDuration() {
        int laps = 5;
        int duration = 30;
        Pricing pricing = new Pricing();
        pricing.setLaps(laps);
        pricing.setBasePrice(1000.0);
        pricing.setTotalDuration(duration);

        when(pricingRepository.findByLapsAndTotalDuration(laps, duration)).thenReturn(Optional.of(pricing));

        Pricing foundPricing = pricingServices.getPricingByLapsAndDuration(laps, duration);

        assertNotNull(foundPricing);
        assertEquals(laps, foundPricing.getLaps());
        assertEquals(duration, foundPricing.getTotalDuration());
        verify(pricingRepository).findByLapsAndTotalDuration(laps, duration);
    }

    @Test
    public void testGetPricingByLapsAndDurationNotFound() {
        int laps = 5;
        int duration = 30;

        when(pricingRepository.findByLapsAndTotalDuration(laps, duration)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pricingServices.getPricingByLapsAndDuration(laps, duration);
        });

        assertEquals("Tarifa no encontrada para esos valores", exception.getMessage());
    }

    @Test
    public void testGetDurationByLaps() {
        int laps = 5;
        int duration = 30;

        when(pricingRepository.findDurationByLaps(laps)).thenReturn(Optional.of(duration));

        int foundDuration = pricingServices.getDurationByLaps(laps);

        assertEquals(duration, foundDuration);
        verify(pricingRepository).findDurationByLaps(laps);
    }

    @Test
    public void testGetDurationByLapsNotFound() {
        int laps = 5;

        when(pricingRepository.findDurationByLaps(laps)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pricingServices.getDurationByLaps(laps);
        });

        assertEquals("No se encontró duración para esas vueltas", exception.getMessage());
    }

    @Test
    public void testSavePricing() {
        Pricing pricing = new Pricing();
        pricing.setLaps(5);
        pricing.setBasePrice(1000.0);
        pricing.setTotalDuration(30);

        when(pricingRepository.save(any(Pricing.class))).thenReturn(pricing);

        Pricing savedPricing = pricingServices.savePricing(pricing);

        assertNotNull(savedPricing);
        assertEquals(pricing.getLaps(), savedPricing.getLaps());
        verify(pricingRepository).save(any(Pricing.class));
    }

    @Test
    public void testUpdatePricing() {
        Long pricingId = 1L;
        Pricing existingPricing = new Pricing();
        existingPricing.setLaps(5);
        existingPricing.setBasePrice(1000.0);
        existingPricing.setTotalDuration(30);

        Pricing updatedPricingDetails = new Pricing();
        updatedPricingDetails.setLaps(10);
        updatedPricingDetails.setBasePrice(1200.0);
        updatedPricingDetails.setTotalDuration(40);

        when(pricingRepository.findById(pricingId)).thenReturn(Optional.of(existingPricing));
        when(pricingRepository.save(any(Pricing.class))).thenReturn(updatedPricingDetails);

        Pricing updatedPricing = pricingServices.updatePricing(pricingId, updatedPricingDetails);

        assertNotNull(updatedPricing);
        assertEquals(updatedPricingDetails.getLaps(), updatedPricing.getLaps());
        assertEquals(updatedPricingDetails.getBasePrice(), updatedPricing.getBasePrice());
        assertEquals(updatedPricingDetails.getTotalDuration(), updatedPricing.getTotalDuration());
        verify(pricingRepository).save(any(Pricing.class));
    }

    @Test
    public void testDeletePricing() {
        Long pricingId = 1L;
        Pricing pricing = new Pricing();
        pricing.setLaps(5);
        pricing.setBasePrice(1000.0);
        pricing.setTotalDuration(30);

        when(pricingRepository.findById(pricingId)).thenReturn(Optional.of(pricing));

        pricingServices.deletePricing(pricingId);

        verify(pricingRepository).delete(pricing);
    }

    @Test
    public void testDeletePricingNotFound() {
        Long pricingId = 1L;

        when(pricingRepository.findById(pricingId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            pricingServices.deletePricing(pricingId);
        });

        assertEquals("Tarifa no encontrada con ID: " + pricingId, exception.getMessage());
    }
}
