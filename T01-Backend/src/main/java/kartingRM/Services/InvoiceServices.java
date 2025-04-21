package kartingRM.Services;

import kartingRM.Entities.Booking;
import kartingRM.Entities.Invoice;
import kartingRM.Repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceServices {
    @Autowired
    private InvoiceRepository invoiceRepository;

    public Invoice generateInvoice(Booking booking) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setBooking(booking);
        invoice.setClientName(booking.getClient().getName());
        invoice.setClientEmail(booking.getClient().getEmail());
        invoice.setParticipantCount(booking.getParticipants().size());
        invoice.setTotalWithTaxes(booking.getTotal());

        // En una implementación real, aquí generaría el PDF y guardaría la ruta
        invoice.setPdfFilePath("/invoices/" + invoice.getInvoiceNumber() + ".pdf");

        return invoiceRepository.save(invoice);
    }

    public Optional<Invoice> findByBookingId(Long bookingId) {
        return invoiceRepository.findByBookingId(bookingId);
    }
}