package kartingRM.DTOs;

import kartingRM.Entities.Invoice;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class InvoiceListDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDateTime issueDate;
    private String clientName;
    private String clientEmail;
    private String reservationCode;
    private double subtotal;
    private double iva;
    private Double totalToPay;
    private boolean pdfGenerated;
    private List<AppliedDiscountDTO> appliedDiscounts;

    public static InvoiceListDTO fromEntity(Invoice invoice) {
        InvoiceListDTO dto = new InvoiceListDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setClientName(invoice.getClientName());
        dto.setClientEmail(invoice.getClientEmail());
        dto.setReservationCode(invoice.getBooking().getReservationCode());
        dto.setSubtotal(invoice.getSubtotal());
        dto.setIva(invoice.getIva());
        dto.setTotalToPay(invoice.getTotalToPay());
        dto.setPdfGenerated(invoice.isPdfGenerated());

        // Mapeo de descuentos aplicados
        dto.setAppliedDiscounts(invoice.getAppliedDiscounts().stream()
                .map(AppliedDiscountDTO::fromEntity)
                .collect(Collectors.toList()));

        return dto;
    }
}