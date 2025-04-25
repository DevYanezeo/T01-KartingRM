package kartingRM.Services;

import kartingRM.Entities.Booking;
import kartingRM.Entities.Invoice;
import kartingRM.Repositories.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServices {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServices(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
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
        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setClientName(booking.getOwner().getName());
        invoice.setClientEmail(booking.getOwner().getEmail());
        invoice.setTotalToPay(totalToPay);
        invoice.setPdfFilePath(pdfPath); // en esta etapa puede ser un placeholder

        return invoiceRepository.save(invoice);
    }

    private String generateInvoiceNumber() {
        long count = invoiceRepository.count() + 1;
        return String.format("INV-%06d", count);
    }
}
