package kartingRM.Controllers;

import kartingRM.DTOs.KartDTO;
import kartingRM.Entities.Kart;
import kartingRM.Services.KartServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/kart")
@CrossOrigin(origins = "*")
public class KartController {

    @Autowired
    private KartServices kartServices;

    @GetMapping
    public ResponseEntity<List<KartDTO>> getAllKarts() {
        return ResponseEntity.ok(kartServices.getAllKartsWithRentals());
    }

    @GetMapping("/available")
    public ResponseEntity<List<KartDTO>> getAvailableKarts() {
        return ResponseEntity.ok(kartServices.getAvailableKartsWithRentals());
    }

    @PutMapping("/{kartCode}/maintenance")
    public ResponseEntity<KartDTO> updateMaintenanceStatus(
            @PathVariable String kartCode,
            @RequestParam boolean underMaintenance) {
        return ResponseEntity.ok(
                kartServices.updateMaintenanceStatusWithRentalCount(kartCode, underMaintenance)
        );
    }

}