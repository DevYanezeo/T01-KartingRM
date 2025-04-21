package kartingRM.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Kart")
public class Kart {
    @Id
    @Column(name = "kart_code")
    private String kartCode;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private boolean underMaintenance = false;

    private String description;

    // Relación con reservas
    @ManyToMany(mappedBy = "assignedKarts")
    private List<Booking> bookings = new ArrayList<>();

}
