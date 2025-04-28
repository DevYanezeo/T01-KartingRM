package KartingRM.Services;

import kartingRM.Entities.Kart;
import kartingRM.Repositories.KartRepository;
import kartingRM.DTOs.KartDTO;
import kartingRM.Services.KartServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class KartServicesTest {

    @Mock
    private KartRepository kartRepository;

    private KartServices kartServices;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        kartServices = new KartServices(kartRepository);
    }

    @Test
    public void testRegisterKart() {
        // Simula la existencia de un kart con código específico
        String kartCode = "KART123";
        String model = "Model A";
        boolean underMaintenance = false;

        when(kartRepository.existsByKartCode(kartCode)).thenReturn(false); // Kart no existe

        Kart kart = new Kart();
        kart.setKartCode(kartCode);
        kart.setModel(model);
        kart.setUnderMaintenance(underMaintenance);

        when(kartRepository.save(any(Kart.class))).thenReturn(kart);

        Kart registeredKart = kartServices.registerKart(kartCode, model, underMaintenance);

        assertNotNull(registeredKart);
        assertEquals(kartCode, registeredKart.getKartCode());
        assertEquals(model, registeredKart.getModel());
        assertEquals(underMaintenance, registeredKart.isUnderMaintenance());
        verify(kartRepository).save(any(Kart.class));
    }

    @Test
    public void testRegisterKartAlreadyExists() {
        String kartCode = "KART123";
        when(kartRepository.existsByKartCode(kartCode)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            kartServices.registerKart(kartCode, "Model A", false);
        });

        assertEquals("Kart with code " + kartCode + " already exists", exception.getMessage());
    }

    @Test
    public void testUpdateMaintenanceStatus() {
        String kartCode = "KART123";
        boolean underMaintenance = true;
        Kart kart = new Kart();
        kart.setKartCode(kartCode);

        when(kartRepository.findByKartCode(kartCode)).thenReturn(Optional.of(kart));
        when(kartRepository.save(any(Kart.class))).thenReturn(kart);

        Kart updatedKart = kartServices.updateMaintenanceStatus(kartCode, underMaintenance);

        assertTrue(updatedKart.isUnderMaintenance());
        verify(kartRepository).save(any(Kart.class));
    }

    @Test
    public void testUpdateMaintenanceStatusKartNotFound() {
        String kartCode = "KART123";
        boolean underMaintenance = true;

        when(kartRepository.findByKartCode(kartCode)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            kartServices.updateMaintenanceStatus(kartCode, underMaintenance);
        });

        assertEquals("Kart not found with code: " + kartCode, exception.getMessage());
    }

    @Test
    public void testAssignKartsForBooking() {
        String kartCode = "KART123";
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.of(10, 0);
        int duration = 30; // minutes
        int kartsNeeded = 2;

        Kart kart = new Kart();
        kart.setKartCode(kartCode);

        List<Kart> availableKarts = Arrays.asList(kart, kart);

        when(kartRepository.findAvailableKarts(date, startTime, startTime.plusMinutes(duration)))
                .thenReturn(availableKarts);

        List<Kart> assignedKarts = kartServices.assignKartsForBooking(date, startTime, duration, kartsNeeded);

        assertEquals(kartsNeeded, assignedKarts.size());
        assertEquals(kartCode, assignedKarts.get(0).getKartCode());
        verify(kartRepository).saveAll(anyList());
    }

    @Test
    public void testAssignKartsForBookingNotEnoughKarts() {
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.of(10, 0);
        int duration = 30; // minutes
        int kartsNeeded = 3;

        List<Kart> availableKarts = Arrays.asList(new Kart(), new Kart());

        when(kartRepository.findAvailableKarts(date, startTime, startTime.plusMinutes(duration)))
                .thenReturn(availableKarts);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            kartServices.assignKartsForBooking(date, startTime, duration, kartsNeeded);
        });

        assertEquals("No hay suficientes karts disponibles. Requeridos: 3, Disponibles: 2", exception.getMessage());
    }

    @Test
    public void testGetAllKartsWithRentals() {
        Kart kart = new Kart();
        kart.setKartCode("KART123");
        kart.setModel("Model A");
        kart.setUnderMaintenance(false);

        List<Kart> karts = Arrays.asList(kart);
        when(kartRepository.findAll()).thenReturn(karts);

        List<KartDTO> kartDTOs = kartServices.getAllKartsWithRentals();

        assertEquals(1, kartDTOs.size());
        assertEquals("KART123", kartDTOs.get(0).getKartCode());
        assertEquals("Model A", kartDTOs.get(0).getModel());
    }

    @Test
    public void testGetAvailableKartsWithRentals() {
        Kart kart = new Kart();
        kart.setKartCode("KART123");
        kart.setModel("Model A");
        kart.setUnderMaintenance(false);

        List<Kart> availableKarts = Arrays.asList(kart);
        when(kartRepository.findAvailableKartsOnlyByMaintenance()).thenReturn(availableKarts);

        List<KartDTO> availableKartDTOs = kartServices.getAvailableKartsWithRentals();

        assertEquals(1, availableKartDTOs.size());
        assertEquals("KART123", availableKartDTOs.get(0).getKartCode());
    }

    @Test
    public void testUpdateMaintenanceStatusWithRentalCount() {
        String kartCode = "KART123";
        boolean underMaintenance = true;
        Kart kart = new Kart();
        kart.setKartCode(kartCode);
        kart.setUnderMaintenance(false);

        when(kartRepository.findByKartCode(kartCode)).thenReturn(Optional.of(kart));
        when(kartRepository.save(any(Kart.class))).thenReturn(kart);

        KartDTO updatedKartDTO = kartServices.updateMaintenanceStatusWithRentalCount(kartCode, underMaintenance);

        assertTrue(updatedKartDTO.isUnderMaintenance());
        assertEquals(LocalDate.now(), updatedKartDTO.getLastMaintenance());
        verify(kartRepository).save(any(Kart.class));
    }
}
