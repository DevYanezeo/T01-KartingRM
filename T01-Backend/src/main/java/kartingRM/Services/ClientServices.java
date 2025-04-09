package kartingRM.Services;

import kartingRM.Entities.Client;
import kartingRM.Repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ClientServices {
    @Autowired
    private ClientRepository clientRepository;

    public Client registerClient(String clientName, String clientEmail, int monthlyVisits, Date birthDate) {
        Optional<Client> existingClient = clientRepository.findByClientEmail(clientEmail);

        if (existingClient.isPresent()) {
            return existingClient.get();
        }

        Client client = new Client(clientName, clientEmail, monthlyVisits, birthDate);
        return clientRepository.save(client);
    }
}
