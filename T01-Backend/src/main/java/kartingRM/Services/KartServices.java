package kartingRM.Services;

import kartingRM.Entities.Kart;
import kartingRM.Entities.Kart.KartStatus;
import kartingRM.Repositories.KartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class KartServices {
    @Autowired
    private KartRepository kartRepository;

    // Registro de kart (ahora con estado DISPONIBLE por defecto)
    public Kart registerKart(String kartCode, String model, boolean underMaintenance) {
        if (kartRepository.existsByKartCode(kartCode)) {
            throw new IllegalArgumentException("Kart with code " + kartCode + " already exists");
        }

        Kart kart = new Kart();
        kart.setKartCode(kartCode);
        kart.setModel(model);
        kart.setUnderMaintenance(underMaintenance);
        kart.setStatus(underMaintenance ? KartStatus.EN_MANTENIMIENTO : KartStatus.DISPONIBLE);

        return kartRepository.save(kart);
    }

    // Obtener todos los karts
    public List<Kart> getAllKarts() {
        return kartRepository.findAll();
    }

    // Obtener karts disponibles (DISPONIBLE y sin mantenimiento)
    public List<Kart> getAvailableKarts() {
        return kartRepository.findAvailableKartsOnlyByMaintenance();
    }

    // Actualizar mantenimiento (y estado asociado)
    public Kart updateMaintenanceStatus(String kartCode, boolean underMaintenance) {
        Kart kart = kartRepository.findByKartCode(kartCode)
                .orElseThrow(() -> new IllegalArgumentException("Kart not found"));

        kart.setUnderMaintenance(underMaintenance);
        kart.setStatus(underMaintenance ? KartStatus.EN_MANTENIMIENTO : KartStatus.DISPONIBLE);

        return kartRepository.save(kart);
    }

    // --- Nuevos métodos para manejo de estados ---
    // Reservar kart
    public Kart reserveKart(String kartCode) {
        Kart kart = kartRepository.findByKartCode(kartCode)
                .orElseThrow(() -> new IllegalArgumentException("Kart not found"));

        if (kart.getStatus() != KartStatus.DISPONIBLE) {
            throw new IllegalStateException("Kart is not available for reservation");
        }

        kart.setStatus(KartStatus.RESERVADO);
        return kartRepository.save(kart);
    }

    // Liberar kart
    public Kart releaseKart(String kartCode) {
        Kart kart = kartRepository.findByKartCode(kartCode)
                .orElseThrow(() -> new IllegalArgumentException("Kart not found"));

        kart.setStatus(KartStatus.DISPONIBLE);
        return kartRepository.save(kart);
    }

    // Buscar por estado
    public List<Kart> findByStatus(KartStatus status) {
        return kartRepository.findByStatus(status);
    }


}