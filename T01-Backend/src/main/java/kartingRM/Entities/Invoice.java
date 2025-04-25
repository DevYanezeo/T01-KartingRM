package kartingRM.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber; // Ej: INV-000123

    @Column(nullable = false)
    private LocalDateTime issueDate;

    @OneToOne
    @JoinColumn(nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false)
    private String clientEmail;

    @Column(nullable = false)
    private Double totalToPay;

    // Se generará después
    @Column(nullable = false)
    private String pdfFilePath;

}
