package kartingRM.Controllers;

import kartingRM.Entities.Invoice;
import kartingRM.Services.InvoiceServices;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceServices invoiceService;

    public InvoiceController(InvoiceServices invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getById(@PathVariable Long id) {
        return invoiceService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-number/{number}")
    public ResponseEntity<Invoice> getByInvoiceNumber(@PathVariable String number) {
        return invoiceService.getByInvoiceNumber(number)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {
        return invoiceService.getById(id)
                .map(invoice -> {
                    if (!invoice.isPdfGenerated()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("PDF no generado aún".getBytes());
                    }

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=comprobante_" + invoice.getInvoiceNumber() + ".pdf")
                            .body(invoice.getPdfData());
                })
                .orElse(ResponseEntity.notFound().build());
    }


}
