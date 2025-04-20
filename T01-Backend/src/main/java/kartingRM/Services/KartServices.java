package kartingRM.Services;

import kartingRM.Entities.Kart;
import kartingRM.Repositories.KartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class KartServices {
    @Autowired
    private KartRepository kartRepository;

    public Kart registerKart(String kartCode, String model, boolean status) {
        Optional<Kart> existingKart = kartRepository.findByKartCode(kartCode);

        if (existingKart.isPresent()) {
            throw new IllegalArgumentException("Kart with code " + kartCode + " already exists");
        }

        Kart kart = new Kart();
        kart.setKartCode(kartCode);
        kart.setModel(model);
        kart.setUnderMaintenance(status);

        return kartRepository.save(kart);
    }

    /**
     * Obtiene todos los karts
     */
    public List<Kart> getAllKarts() {
        return kartRepository.findAll();
    }

    /**
     * Obtiene karts disponibles (status=true y underMaintenance=false)
     */
    public List<Kart> getAvailableKarts() {
        return kartRepository.findByStatusTrueAndUnderMaintenanceFalse();
    }

    /**
     * Actualiza el estado de mantenimiento de un kart
     */
    public Kart updateMaintenanceStatus(String kartCode, boolean underMaintenance) {
        Kart kart = kartRepository.findByKartCode(kartCode)
                .orElseThrow(() -> new IllegalArgumentException("Kart not found"));

        kart.setUnderMaintenance(underMaintenance);
        if (underMaintenance) {
            kart.setUnderMaintenance(false);
        }

        return kartRepository.save(kart);
    }




}

