package kartingRM.DTOs;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClientRequest {
    private String name;
    private String email;
    private LocalDate birthDate;    // Para validar descuentos por cumpleaños
}