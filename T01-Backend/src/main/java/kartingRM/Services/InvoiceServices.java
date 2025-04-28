package kartingRM.Services;

import kartingRM.Config.TaxConfiguration;
import kartingRM.Entities.*;
import kartingRM.Repositories.AppliedDiscountRepository;
import kartingRM.Repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServices {
    private final InvoiceRepository invoiceRepository;
    private final AppliedDiscountRepository appliedDiscountRepository;
    private final PDFGeneratorServices pdfGenerator;
    private final TaxConfiguration taxConfiguration;
    private final DiscountServices discountServices;

    public Invoice generateAndSaveInvoice(Booking booking, double baseTotal) {
        // 1. Crear factura base
        Invoice invoice = createBasicInvoice(booking, baseTotal);

        // 2. Aplicar descuentos y calcular totales
        applyDiscountsAndCalculateTotals(invoice, booking);

        // 3. Generar PDF
        generateInvoicePdf(invoice, booking);

        return invoiceRepository.save(invoice);
    }

    private Invoice createBasicInvoice(Booking booking, double baseRate) {
        return Invoice.builder()
                .booking(booking)
                .invoiceNumber(generateInvoiceNumber())
                .issueDate(LocalDateTime.now())
                .baseRate(baseRate)
                .clientName(booking.getOwner().getName())
                .clientEmail(booking.getOwner().getEmail())
                .pdfGenerated(false)
                .appliedDiscounts(new ArrayList<>())
                .build();
    }

    public void applyDiscountsAndCalculateTotals(Invoice invoice, Booking booking) {
        List<AppliedDiscount> appliedDiscounts = new ArrayList<>();
        double subtotal = invoice.getBaseRate();

        List<Client> allParticipants = new ArrayList<>(booking.getParticipants());
        allParticipants.add(booking.getOwner());

        for (Client participant : allParticipants) {
            // Descuento por cliente frecuente
            if (participant.getMonthlyVisits() >= 2) {
                appliedDiscounts.add(createFrequentClientDiscount(invoice, participant, booking.getPricing().getBasePrice()));
            }

            // Descuento por cumpleaños
            if (participant.isBirthdayToday() && isEligibleForBirthdayDiscount(participant, booking)) {
                appliedDiscounts.add(createBirthdayDiscount(invoice, participant, booking.getPricing().getBasePrice()));
            }
        }

        // Calcular subtotal después de descuentos individuales
        double individualDiscounts = appliedDiscounts.stream()
                .mapToDouble(AppliedDiscount::getDiscountAmount)
                .sum();
        double subtotalAfterIndividualDiscounts = subtotal - individualDiscounts;

        // Aplicar descuento por grupo si corresponde
        int groupSize = allParticipants.size();
        double groupDiscountAmount = 0;

        if (groupSize >= 3) {
            AppliedDiscount groupDiscount = createGroupDiscount(invoice, groupSize, subtotalAfterIndividualDiscounts);
            appliedDiscounts.add(groupDiscount);
            groupDiscountAmount = groupDiscount.getDiscountAmount();
        }

        // Calcular totales finales
        double subtotalAfterAllDiscounts = subtotalAfterIndividualDiscounts - groupDiscountAmount;
        double iva = taxConfiguration.calculateIva(subtotalAfterAllDiscounts);
        double totalToPay = subtotalAfterAllDiscounts + iva;

        // Guardar descuentos y actualizar factura
        invoice.setAppliedDiscounts(appliedDiscountRepository.saveAll(appliedDiscounts));
        invoice.setSubtotal(subtotalAfterAllDiscounts);
        invoice.setIva(iva);
        invoice.setTotalToPay(totalToPay);
    }


    private List<AppliedDiscount> createParticipantDiscounts(Invoice invoice, Booking booking) {
        List<AppliedDiscount> discounts = new ArrayList<>();
        double basePrice = booking.getPricing().getBasePrice();

        // Añadir todos los participantes (incluido el propietario)
        List<Client> allParticipants = new ArrayList<>(booking.getParticipants());
        allParticipants.add(booking.getOwner());

        // Para cada participante, aplicar descuentos individuales
        for (Client participant : allParticipants) {
            // 1. Descuento por cliente frecuente (si aplica)
            if (participant.getMonthlyVisits() >= 2) { // Cualquier categoría que no sea NO_FRECUENTE
                discounts.add(createFrequentClientDiscount(invoice, participant, basePrice));
            }

            // 2. Descuento por cumpleaños (si aplica)
            if (participant.isBirthdayToday() && isEligibleForBirthdayDiscount(participant, booking)) {
                discounts.add(createBirthdayDiscount(invoice, participant, basePrice));
            }
        }

        return discounts;
    }

    private boolean isEligibleForBirthdayDiscount(Client client, Booking booking) {
        int groupSize = booking.getParticipants().size() + 1; // Incluye al propietario

        // Límite de descuentos por cumpleaños según tamaño del grupo
        int maxBirthdayDiscounts = getMaxBirthdayDiscounts(groupSize);

        if (maxBirthdayDiscounts == 0) {
            return false;
        }

        // Contar cuántos descuentos por cumpleaños ya se han aplicado a participantes con IDs menores
        List<Client> allParticipants = new ArrayList<>(booking.getParticipants());
        allParticipants.add(booking.getOwner());

        long birthdayDiscountsAlreadyApplied = allParticipants.stream()
                .filter(p -> p.getId() < client.getId())
                .filter(Client::isBirthdayToday)
                .count();

        return birthdayDiscountsAlreadyApplied < maxBirthdayDiscounts;
    }

    private AppliedDiscount createGroupDiscount(Invoice invoice, int groupSize, double subtotalAfterIndividualDiscounts) {
        double discountPercentage = discountServices.getApplicableGroupDiscount(groupSize);
        double discountAmount = subtotalAfterIndividualDiscounts * discountPercentage;

        return AppliedDiscount.builder()
                .invoice(invoice)
                .appliedDescription(String.format("Descuento por grupo (%d personas)", groupSize))
                .originalAmount(subtotalAfterIndividualDiscounts)
                .discountAmount(discountAmount)
                .discountPercentage(discountPercentage)
                .discountType(AppliedDiscount.DiscountType.GROUP)
                .isAppliedToTotal(true)
                .appliedDate(LocalDateTime.now())
                .build();
    }

    private AppliedDiscount createFrequentClientDiscount(Invoice invoice, Client client, double basePrice) {

        // Verificar que el cliente no tenga ya este descuento
        boolean alreadyHasDiscount = invoice.getAppliedDiscounts().stream()
                .anyMatch(d -> d.getClient() != null &&
                        d.getClient().equals(client) &&
                        d.getDiscountType() == AppliedDiscount.DiscountType.FREQUENT_CLIENT);

        if (alreadyHasDiscount) {
            throw new IllegalStateException("El cliente ya tiene un descuento por frecuencia aplicado");
        }

        double discountPercentage = discountServices.getApplicableFrequentClientDiscount(client.getMonthlyVisits());
        double discountAmount = basePrice * discountPercentage;

        return AppliedDiscount.builder()
                .invoice(invoice)
                .client(client) // Importante! Aseguramos asignar el cliente al descuento
                .appliedDescription(String.format("Descuento cliente frecuente (%d visitas)", client.getMonthlyVisits()))
                .originalAmount(basePrice)
                .discountAmount(discountAmount)
                .discountPercentage(discountPercentage)
                .discountType(AppliedDiscount.DiscountType.FREQUENT_CLIENT)
                .isAppliedToTotal(false)
                .appliedDate(LocalDateTime.now())
                .build();
    }

    private AppliedDiscount createBirthdayDiscount(Invoice invoice, Client client, double basePrice) {
        return AppliedDiscount.builder()
                .invoice(invoice)
                .client(client) // Importante! Aseguramos asignar el cliente al descuento
                .appliedDescription("Descuento por cumpleaños")
                .originalAmount(basePrice)
                .discountAmount(basePrice * 0.5) // 50%
                .discountPercentage(0.5) // 50%
                .discountType(AppliedDiscount.DiscountType.BIRTHDAY)
                .isAppliedToTotal(false)
                .appliedDate(LocalDateTime.now())
                .build();
    }

    private int getMaxBirthdayDiscounts(int groupSize) {
        if (groupSize >= 3 && groupSize <= 5) return 1;
        if (groupSize >= 6 && groupSize <= 10) return 2;
        return 0;
    }

    // Métodos existentes que se mantienen igual
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> getById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Optional<Invoice> getByInvoiceNumber(String number) {
        return invoiceRepository.findByInvoiceNumber(number);
    }

    private void generateInvoicePdf(Invoice invoice, Booking booking) {
        try {
            byte[] pdfBytes = pdfGenerator.generateBasicInvoice(booking, invoice);
            invoice.setPdfData(pdfBytes);
            invoice.setPdfGenerated(true);
            invoice.setPdfFilePath("/invoices/" + invoice.getInvoiceNumber() + ".pdf");
        } catch (IOException e) {
            invoice.setPdfGenerated(false);
            invoice.setPdfFilePath(null);
        }
    }

    private Map<String, Double> createDiscountSummary(Invoice invoice) {
        Map<String, Double> summary = new LinkedHashMap<>();

        // Agrupar descuentos por tipo
        Map<AppliedDiscount.DiscountType, Double> discountsByType = invoice.getAppliedDiscounts().stream()
                .collect(Collectors.groupingBy(
                        AppliedDiscount::getDiscountType,
                        Collectors.summingDouble(AppliedDiscount::getDiscountAmount)
                ));

        // Convertir a formato legible para el PDF
        discountsByType.forEach((type, amount) -> {
            String description = type.getDescription(); // Usa el description del enum
            summary.put(description, amount);
        });

        return summary;
    }

    public String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}