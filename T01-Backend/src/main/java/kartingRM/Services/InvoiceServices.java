package kartingRM.Services;

import jakarta.transaction.Transactional;
import kartingRM.Entities.Booking;
import kartingRM.Entities.Invoice;
import kartingRM.Repositories.InvoiceRepository;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class InvoiceServices {
    // Dependencias
    private final InvoiceRepository invoiceRepository;
    private final PDFGeneratorServices pdfGenerator;

    // Constructor
    public InvoiceServices(InvoiceRepository invoiceRepository, PDFGeneratorServices pdfGenerator) {
        this.invoiceRepository = invoiceRepository;
        this.pdfGenerator = pdfGenerator;
    }

    // --------------------------
    // Métodos Públicos Principales
    // --------------------------

    public Invoice generateAndSaveInvoice(Booking booking, double totalToPay,
                                          Map<String, Double> discountSummary) {
        Invoice invoice = createBasicInvoice(booking, totalToPay);

        // Opcional: Guardar el resumen como JSON si necesitas persistirlo
        // invoice.setDiscountSummaryJson(convertToJson(discountSummary));

        generateInvoicePdf(invoice, booking, discountSummary);
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> getById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Optional<Invoice> getByInvoiceNumber(String number) {
        return invoiceRepository.findByInvoiceNumber(number);
    }

    public Invoice saveInvoice(Booking booking, double totalToPay, String pdfPath) {
        Invoice invoice = createBasicInvoice(booking, totalToPay);
        invoice.setPdfFilePath(pdfPath);
        return invoiceRepository.save(invoice);
    }

    // --------------------------
    // Métodos Privados de Apoyo
    // --------------------------

    private Invoice createBasicInvoice(Booking booking, double totalToPay) {
        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setClientName(booking.getOwner().getName());
        invoice.setClientEmail(booking.getOwner().getEmail());
        invoice.setTotalToPay(totalToPay);
        invoice.setPdfGenerated(false);
        return invoice;
    }

    private void generateInvoicePdf(Invoice invoice, Booking booking,
                                    Map<String, Double> discountSummary) {
        try {
            byte[] pdfBytes = pdfGenerator.generateBasicInvoice(
                    booking,
                    invoice,
                    discountSummary
            );
            invoice.setPdfData(pdfBytes);
            invoice.setPdfGenerated(true);
            invoice.setPdfFilePath("/invoices/" + invoice.getInvoiceNumber() + ".pdf");
        } catch (IOException e) {
            invoice.setPdfGenerated(false);
            invoice.setPdfFilePath(null);
        }
    }

    private String generateInvoiceNumber() {
        long count = invoiceRepository.count() + 1;
        return String.format("INV-%06d", count);
    }
}