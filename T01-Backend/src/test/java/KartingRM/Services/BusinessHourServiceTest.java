package KartingRM.Services;

import kartingRM.Entities.BusinessHour;
import kartingRM.Repositories.BusinessHourRepository;
import kartingRM.Services.BusinessHourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusinessHourServiceTest {

    @Mock
    private BusinessHourRepository businessHourRepository;

    @InjectMocks
    private BusinessHourService businessHourService;

    private BusinessHour businessHour;

    @BeforeEach
    public void setUp() {
        // Configura los valores predeterminados de horario de trabajo
        businessHour = new BusinessHour();
        businessHour.setDayOfWeek(DayOfWeek.MONDAY);
        businessHour.setOpeningTime(LocalTime.of(9, 0));  // 9:00 AM
        businessHour.setClosingTime(LocalTime.of(18, 0)); // 6:00 PM
    }

    @Test
    public void testIsWithinBusinessHours_ValidTime() {
        // Simula el comportamiento del repositorio
        when(businessHourRepository.findByDayOfWeek(DayOfWeek.MONDAY)).thenReturn(Arrays.asList(businessHour));

        // Verifica que el horario está dentro de los horarios comerciales
        boolean result = businessHourService.isWithinBusinessHours(
                LocalDate.of(2025, 4, 28), // Lunes
                LocalTime.of(10, 0), // 10:00 AM
                LocalTime.of(12, 0)  // 12:00 PM
        );

        assertTrue(result);
    }

    @Test
    public void testIsWithinBusinessHours_InvalidTime() {
        // Simula el comportamiento del repositorio
        when(businessHourRepository.findByDayOfWeek(DayOfWeek.MONDAY)).thenReturn(Arrays.asList(businessHour));

        // Verifica que el horario está fuera del horario comercial
        boolean result = businessHourService.isWithinBusinessHours(
                LocalDate.of(2025, 4, 28), // Lunes
                LocalTime.of(8, 0),  // 8:00 AM (Antes de la apertura)
                LocalTime.of(12, 0)  // 12:00 PM
        );

        assertFalse(result);
    }

    @Test
    public void testIsWithinBusinessHours_NoBusinessHoursConfigured() {
        // Simula el comportamiento del repositorio cuando no se configuran horarios
        when(businessHourRepository.findByDayOfWeek(DayOfWeek.MONDAY)).thenReturn(List.of());

        // Verifica que se lanza una excepción cuando no hay horarios configurados
        assertThrows(RuntimeException.class, () -> businessHourService.isWithinBusinessHours(
                LocalDate.of(2025, 4, 28), // Lunes
                LocalTime.of(10, 0), // 10:00 AM
                LocalTime.of(12, 0)  // 12:00 PM
        ));
    }
}
