package kartingRM.Services;

import kartingRM.Entities.Kart;
import kartingRM.Repositories.KartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KartServices {
    @Autowired
    private KartRepository kartRepository;

    public Kart registerKart(String kartCode, String model, String status) {
        Optional<Kart> existingKart = kartRepository.findByKartCode(kartCode);

        if (existingKart.isPresent()) {
            throw new IllegalArgumentException("Duplicate");
        }

        Kart kart = new Kart();
        kart.setKartCode(kartCode);
        kart.setModel(model);
        kart.setStatus(status);

        return kartRepository.save(kart);
    }
}

