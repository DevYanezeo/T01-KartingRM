package kartingRM.Entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private Integer monthlyVisits = 0;

    @OneToMany(mappedBy = "owner")
    private List<Booking> bookings = new ArrayList<>();

    // Método para determinar la categoría del cliente según visitas mensuales (PDF página 3)
    public String getClientCategory() {
        if (monthlyVisits >= 7) return "MUY_FRECUENTE";
        if (monthlyVisits >= 5) return "FRECUENTE";
        if (monthlyVisits >= 2) return "REGULAR";
        return "NO_FRECUENTE";
    }

    // Método para incrementar visitas mensuales
    public void incrementMonthlyVisits() {
        this.monthlyVisits += 1;
    }


    // Método para verificar si hoy es su cumpleaños (PDF página 3)
    public boolean isBirthdayToday() {
        LocalDate today = LocalDate.now();
        return birthDate.getMonth() == today.getMonth()
                && birthDate.getDayOfMonth() == today.getDayOfMonth();
    }
}