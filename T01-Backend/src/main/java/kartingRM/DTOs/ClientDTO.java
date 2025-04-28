package kartingRM.DTOs;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClientDTO {
    private Long id;
    private String name;
    private String email;
    private String birthDate;
    private int monthlyVisits;

}
