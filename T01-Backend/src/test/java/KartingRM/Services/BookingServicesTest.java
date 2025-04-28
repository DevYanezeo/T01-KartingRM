package KartingRM.Services;

import kartingRM.DTOs.BookingRequest;
import kartingRM.Entities.*;
import kartingRM.Repositories.BookingRepository;
import kartingRM.Services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class BookingServicesTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ClientServices clientServices;
    @Mock
    private DiscountServices discountServices;
    @Mock
    private InvoiceServices invoiceServices;
    @Mock
    private KartServices kartServices;
    @Mock
    private PricingServices pricingServices;
    @Mock
    private BusinessHourService businessHourService;

    private BookingServices bookingServices;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingServices = new BookingServices(bookingRepository, clientServices, discountServices, invoiceServices,
                kartServices, pricingServices, businessHourService);
    }

    @Test
    public void testCreateBooking_Success() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setOwnerId(1L);
        request.setParticipantIds(Arrays.asList(2L, 3L));
        request.setDate(LocalDate.now());
        request.setStartTime(LocalTime.of(10, 0));
        request.setLaps(5);

        Client owner = new Client();
        owner.setId(1L);
        owner.setName("John Doe");

        Client participant1 = new Client();
        participant1.setId(2L);
        participant1.setName("Jane Doe");

        Client participant2 = new Client();
        participant2.setId(3L);
        participant2.setName("Bob Smith");

        List<Client> participants = Arrays.asList(participant1, participant2);

        when(clientServices.validateClientById(1L)).thenReturn(owner);
        when(clientServices.validateClientsByIds(Arrays.asList(2L, 3L))).thenReturn(participants);
        when(businessHourService.isWithinBusinessHours(any(), any(), any())).thenReturn(true);
        when(pricingServices.getDurationByLaps(5)).thenReturn(10);
        when(kartServices.assignKartsForBooking(any(), any(), any(), any())).thenReturn(new ArrayList<>());
        when(pricingServices.getPricingByLapsAndDuration(5, 10)).thenReturn(new Pricing());
        when(bookingRepository.save(any(Booking.class))).thenReturn(new Booking());

        // Act
        Booking result = bookingServices.createBooking(request);

        // Assert
        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void testCreateBooking_Failure_OutOfBusinessHours() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setOwnerId(1L);
        request.setParticipantIds(Arrays.asList(2L, 3L));
        request.setDate(LocalDate.now());
        request.setStartTime(LocalTime.of(10, 0));
        request.setLaps(5);

        when(businessHourService.isWithinBusinessHours(any(), any(), any())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            bookingServices.createBooking(request);
        });
        assertEquals("El horario de la reserva está fuera del horario comercial", thrown.getMessage());
    }

    @Test
    public void testPrepareParticipantsList() {
        // Arrange
        Client owner = new Client();
        owner.setId(1L);
        owner.setName("John Doe");

        Client participant1 = new Client();
        participant1.setId(2L);
        participant1.setName("Jane Doe");

        Client participant2 = new Client();
        participant2.setId(3L);
        participant2.setName("Bob Smith");

        List<Client> participants = Arrays.asList(participant1, participant2);

        // Act
        List<Client> result = bookingServices.prepareParticipantsList(owner, participants);

        // Assert
        assertTrue(result.contains(owner));
        assertTrue(result.contains(participant1));
        assertTrue(result.contains(participant2));
        assertEquals(3, result.size());
    }
}
