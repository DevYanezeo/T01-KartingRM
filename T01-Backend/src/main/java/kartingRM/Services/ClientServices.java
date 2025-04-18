package kartingRM.Services;

import kartingRM.Entities.Client;
import kartingRM.Repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class ClientServices {
    @Autowired
    private ClientRepository clientRepository;

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

}
