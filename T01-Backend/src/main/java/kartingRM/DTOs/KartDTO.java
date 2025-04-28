package kartingRM.DTOs;

import lombok.Data;
import java.time.LocalDate;

@Data
public class KartDTO {
    private String kartCode;
    private String model;
    private boolean underMaintenance;
    private LocalDate lastMaintenance;
    private String description;
}