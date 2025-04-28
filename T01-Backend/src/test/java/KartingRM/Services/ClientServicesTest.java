package KartingRM.Services;

import kartingRM.Entities.Client;
import kartingRM.Repositories.ClientRepository;
import kartingRM.Services.ClientServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.persistence.EntityNotFoundException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class) // Utiliza Mockito para inyectar dependencias
public class ClientServicesTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServices clientServices;

    private Client mockClient;

    @BeforeEach
    public void setUp() {
        mockClient = new Client();
        mockClient.setName("Test Client");
        mockClient.setEmail("test@example.com");
        mockClient.setMonthlyVisits(5);
        mockClient.setBirthDate(LocalDate.of(1990, 5, 10));
    }

    @Test
    public void testRegisterClient_NewClient() {
        // Simula la respuesta del repositorio para que no encuentre el cliente
        when(clientRepository.findByEmail(mockClient.getEmail())).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenReturn(mockClient);

        // Llamada al método que estamos probando
        Client savedClient = clientServices.registerClient(
                mockClient.getName(),
                mockClient.getEmail(),
                mockClient.getMonthlyVisits(),
                mockClient.getBirthDate()
        );

        // Verificar que el cliente se haya registrado correctamente
        assertNotNull(savedClient);
        assertEquals("Test Client", savedClient.getName());
        assertEquals("test@example.com", savedClient.getEmail());
    }

    @Test
    public void testRegisterClient_ExistingClient() {
        // Simula la respuesta del repositorio para que ya exista un cliente
        when(clientRepository.findByEmail(mockClient.getEmail())).thenReturn(Optional.of(mockClient));

        // Llamada al método que estamos probando
        Client existingClient = clientServices.registerClient(
                mockClient.getName(),
                mockClient.getEmail(),
                mockClient.getMonthlyVisits(),
                mockClient.getBirthDate()
        );

        // Verificar que se retorna el cliente existente
        assertEquals(mockClient, existingClient);
    }

    @Test
    public void testValidateClientById_ValidClient() {
        // Simula la respuesta del repositorio con un cliente válido
        when(clientRepository.findById(1L)).thenReturn(Optional.of(mockClient));

        // Llamada al método que estamos probando
        Client client = clientServices.validateClientById(1L);

        // Verificar que el cliente es el esperado
        assertNotNull(client);
        assertEquals(1L, client.getId());
    }

    @Test
    public void testValidateClientById_ClientNotFound() {
        // Simula que no se encuentra el cliente
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Verifica que se lance una excepción cuando el cliente no se encuentre
        assertThrows(EntityNotFoundException.class, () -> clientServices.validateClientById(1L));
    }

    @Test
    public void testIncrementVisits() {
        // Verifica que incrementa las visitas de los clientes
        when(clientRepository.save(any(Client.class))).thenReturn(mockClient);

        clientServices.incrementVisits(List.of(mockClient));

        assertEquals(6, mockClient.getMonthlyVisits()); // El valor esperado es 6 porque se incrementa
        verify(clientRepository, times(1)).save(mockClient); // Verifica que save se llame una vez
    }
}
