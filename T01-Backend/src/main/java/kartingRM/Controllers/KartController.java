package kartingRM.Controllers;

import kartingRM.Entities.Kart;
import kartingRM.Services.KartServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/karts")
@CrossOrigin(origins = "*")
public class KartController {
    @Autowired
    private KartServices kartServices;
    /**
     * Registra un nuevo kart
     */
    @PostMapping
    public ResponseEntity<Kart> registerKart(@RequestBody Kart kart) {
        Kart newKart = kartServices.registerKart(
                kart.getKartCode(),
                kart.getModel(),
                kart.isUnderMaintenance()
        );
        return ResponseEntity.ok(newKart);
    }

    /**
     * Obtiene todos los karts
     */
    @GetMapping
    public ResponseEntity<List<Kart>> getAllKarts() {
        return ResponseEntity.ok(kartServices.getAllKarts());
    }

    /**
     * Obtiene karts disponibles
     */
    @GetMapping("/available")
    public ResponseEntity<List<Kart>> getAvailableKarts() {
        return ResponseEntity.ok(kartServices.getAvailableKarts());
    }

    /**
     * Actualiza estado de mantenimiento
     */
    @PutMapping("/{kartCode}/maintenance")
    public ResponseEntity<Kart> updateMaintenanceStatus(
            @PathVariable String kartCode,
            @RequestParam boolean underMaintenance) {
        return ResponseEntity.ok(
                kartServices.updateMaintenanceStatus(kartCode, underMaintenance)
        );
    }
}