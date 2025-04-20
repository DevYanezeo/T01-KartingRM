package kartingRM.Services;

import kartingRM.Entities.Client;
import kartingRM.Repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClientServices {
    @Autowired
    private ClientRepository clientRepository;

    // Manteniendo tu método original
    public Client registerClient(String clientName, String clientEmail, int monthlyVisits, LocalDate birthDate) {
        Optional<Client> existingClient = clientRepository.findByEmail(clientEmail);

        if (existingClient.isPresent()) {
            return existingClient.get();
        }

        Client client = new Client();
        client.setName(clientName);
        client.setEmail(clientEmail);
        client.setMonthlyVisits(monthlyVisits);
        client.setBirthDate(birthDate);

        return clientRepository.save(client);
    }

    // Nuevos métodos requeridos por el PDF
    public Client incrementMonthlyVisits(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        client.incrementMonthlyVisits();
        return clientRepository.save(client);
    }

    public List<Client> getClientsByCategory(String category) {
        return switch (category.toUpperCase()) {
            case "MUY_FRECUENTE" -> clientRepository.findByMonthlyVisitsGreaterThanEqual(7);
            case "FRECUENTE" -> clientRepository.findByMonthlyVisitsGreaterThanEqual(5);
            case "REGULAR" -> clientRepository.findByMonthlyVisitsGreaterThanEqual(2);
            default -> clientRepository.findByMonthlyVisitsGreaterThanEqual(0);
        };
    }

    public List<Client> getClientsWithBirthdayToday() {
        return clientRepository.findClientsWithBirthdayToday();
    }

}