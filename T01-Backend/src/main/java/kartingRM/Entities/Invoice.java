package kartingRM.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.List;
import java.util.ArrayList;


@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

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
    private double baseRate;

    @Column(nullable = false)
    private double subtotal;

    @Column(nullable = false)
    private double iva;

    @Column(nullable = false)
    private Double totalToPay;

    // Se generará después
    @Column(nullable = false)
    private String pdfFilePath;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] pdfData;

    @Column(nullable = false)
    private boolean pdfGenerated = false;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppliedDiscount> appliedDiscounts = new ArrayList<>();

    public void addAppliedDiscount(AppliedDiscount discount) {
        appliedDiscounts.add(discount);
        discount.setInvoice(this);
    }

}
