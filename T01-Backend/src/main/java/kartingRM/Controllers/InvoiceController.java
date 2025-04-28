package kartingRM.Controllers;

import kartingRM.DTOs.InvoiceListDTO;
import kartingRM.Entities.Invoice;
import kartingRM.Services.InvoiceServices;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceServices invoiceService;

    public InvoiceController(InvoiceServices invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<List<InvoiceListDTO>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();

        List<InvoiceListDTO> dtos = invoices.stream()
                .map(InvoiceListDTO::fromEntity) // Ahora sí usamos el método fromEntity
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
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
