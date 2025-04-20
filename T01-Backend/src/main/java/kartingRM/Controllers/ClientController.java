package kartingRM.Controllers;

import kartingRM.Entities.Client;
import kartingRM.Services.ClientServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "*")
public class ClientController {
    @Autowired
    private ClientServices clientService;

    // Manteniendo tu endpoint original
    @PostMapping("/registerClient")
    public ResponseEntity<Client> addClient(@RequestBody Client newClient) {
        return ResponseEntity.ok(clientService.registerClient(
                newClient.getName(),
                newClient.getEmail(),
                newClient.getMonthlyVisits(),
                newClient.getBirthDate()
        ));
    }

    // Nuevos endpoints requeridos por el PDF
    @PutMapping("/{email}/increment-visits")
    public ResponseEntity<Client> incrementClientVisits(
            @PathVariable String email,
            @RequestHeader(value = "Content-Type", defaultValue = "application/json") String contentType) {

        // Validación básica del email
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Client updatedClient = clientService.incrementMonthlyVisits(email);
            return ResponseEntity.ok(updatedClient);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Client>> getClientsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(clientService.getClientsByCategory(category));
    }

    @GetMapping("/birthday-today")
    public ResponseEntity<List<Client>> getClientsWithBirthdayToday() {
        return ResponseEntity.ok(clientService.getClientsWithBirthdayToday());
    }


}