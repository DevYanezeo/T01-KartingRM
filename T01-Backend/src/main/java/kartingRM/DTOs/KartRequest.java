package kartingRM.DTOs;

import kartingRM.Entities.Kart.KartStatus;
import lombok.Data;

@Data
public class KartRequest {
    private String kartCode;       // Ej: "K001"
    private String model;         // Ej: "Sodikart RT8"
    private KartStatus status;    // DISPONIBLE, RESERVADO, etc.
    private boolean underMaintenance;
}