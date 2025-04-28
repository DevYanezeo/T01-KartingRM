package kartingRM.Services;

import kartingRM.Entities.Kart;
import kartingRM.Repositories.KartRepository;
import kartingRM.DTOs.KartDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class KartServices {
    private final KartRepository kartRepository;

    public Kart registerKart(String kartCode, String model, boolean underMaintenance) {
        if (kartRepository.existsByKartCode(kartCode)) {
            throw new IllegalArgumentException("Kart with code " + kartCode + " already exists");
        }

        Kart kart = new Kart();
        kart.setKartCode(kartCode);
        kart.setModel(model);
        kart.setUnderMaintenance(underMaintenance);

        return kartRepository.save(kart);
    }

    public List<Kart> getAllKarts() {
        return kartRepository.findAll();
    }

    public List<Kart> getAvailableKarts() {
        return kartRepository.findAvailableKartsOnlyByMaintenance();
    }

    public Kart updateMaintenanceStatus(String kartCode, boolean underMaintenance) {
        Kart kart = findKartByCode(kartCode);
        kart.setUnderMaintenance(underMaintenance);
        return kartRepository.save(kart);
    }


    public Kart releaseKart(String kartCode) {
        Kart kart = findKartByCode(kartCode);
        return kartRepository.save(kart);
    }

    // En el servicio
    public List<Kart> assignKartsForBooking(LocalDate date, LocalTime startTime, int duration, int kartsNeeded) {
        // Calculamos el tiempo de finalización
        LocalTime endTime = startTime.plusMinutes(duration);

        // Encontramos los karts disponibles en el rango de tiempo solicitado
        List<Kart> availableKarts = kartRepository.findAvailableKarts(date, startTime, endTime);

        if (availableKarts.size() < kartsNeeded) {
            throw new IllegalStateException(
                    "No hay suficientes karts disponibles. Requeridos: " + kartsNeeded + ", Disponibles: " + availableKarts.size()
            );
        }

        // Seleccionamos los karts para la reserva
        List<Kart> selectedKarts = availableKarts.stream()
                .limit(kartsNeeded)
                .collect(Collectors.toList());

        // Guardamos las reservas
        kartRepository.saveAll(selectedKarts);

        return selectedKarts;
    }


    private Kart findKartByCode(String kartCode) {
        return kartRepository.findByKartCode(kartCode)
                .orElseThrow(() -> new IllegalArgumentException("Kart not found with code: " + kartCode));
    }

    public List<KartDTO> getAllKartsWithRentals() {
        return kartRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<KartDTO> getAvailableKartsWithRentals() {
        return kartRepository.findAvailableKartsOnlyByMaintenance().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public KartDTO updateMaintenanceStatusWithRentalCount(String kartCode, boolean underMaintenance) {
        Kart kart = findKartByCode(kartCode);
        kart.setUnderMaintenance(underMaintenance);

        // Actualiza la fecha solo cuando se envía a mantención
        if (underMaintenance) {
            kart.setLastMaintenance(LocalDate.now());
        }

        return convertToDTO(kartRepository.save(kart));
    }

    private KartDTO convertToDTO(Kart kart) {
        KartDTO dto = new KartDTO();
        dto.setKartCode(kart.getKartCode());
        dto.setModel(kart.getModel());
        dto.setUnderMaintenance(kart.isUnderMaintenance());
        dto.setLastMaintenance(kart.getLastMaintenance());
        dto.setDescription(kart.getDescription());
        return dto;
    }


}