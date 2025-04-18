package kartingRM.Controllers;

import kartingRM.Entities.Client;
import kartingRM.Services.ClientServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "*")
public class ClientController {
    @Autowired
    private ClientServices clientService;

    @PostMapping("/registerClient")
    public Client addClient(@RequestBody Client newClient) {
        return clientService.registerClient(
                newClient.getName(),
                newClient.getEmail(),
                newClient.getMonthlyVisits(),
                newClient.getBirthDate()
        );
    }

}
