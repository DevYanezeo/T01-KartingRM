package kartingRM.Services;

import kartingRM.Config.TaxConfiguration;
import kartingRM.Entities.AppliedDiscount;
import kartingRM.Entities.Booking;
import kartingRM.Entities.Client;
import kartingRM.Entities.Invoice;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PDFGeneratorServices {
    private final TaxConfiguration taxConfiguration;
    private static final float MARGIN = 50;
    private static final float SECTION_SPACING = 30;
    private static final float LINE_SPACING = 20;
    private static final float TABLE_ROW_HEIGHT = 20;
    private static final float TABLE_CELL_MARGIN = 5;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateBasicInvoice(Booking booking, Invoice invoice) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Configuración de fuentes
                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                float yPosition = PDRectangle.A4.getHeight() - MARGIN;

                // 1. Encabezado
                drawCenteredText(contentStream, "KARTINGRM BOLETA DE RESERVA", fontBold, 18, yPosition);
                yPosition -= SECTION_SPACING;

                // 2. Información de la factura
                drawKeyValue(contentStream, "Número:", invoice.getInvoiceNumber(), fontBold, fontNormal, yPosition);
                yPosition -= LINE_SPACING;
                drawKeyValue(contentStream, "Fecha emisión:", invoice.getIssueDate().format(DATETIME_FORMATTER), fontBold, fontNormal, yPosition);
                yPosition -= SECTION_SPACING;

                // 3. Información de la reserva
                drawSectionTitle(contentStream, "Información de la Reserva", fontBold, yPosition);
                yPosition -= SECTION_SPACING/2;

                drawKeyValue(contentStream, "Código reserva:", booking.getReservationCode(), fontBold, fontNormal, yPosition);
                yPosition -= LINE_SPACING;
                drawKeyValue(contentStream, "Fecha reserva:",
                        booking.getDate().format(DATE_FORMATTER) + " " + booking.getStartTime().format(TIME_FORMATTER),
                        fontBold, fontNormal, yPosition);
                yPosition -= LINE_SPACING;
                drawKeyValue(contentStream, "Vueltas/Tiempo:",
                        booking.getLaps() + " vueltas (" + booking.getDuration() + " min)",
                        fontBold, fontNormal, yPosition);
                yPosition -= LINE_SPACING;
                drawKeyValue(contentStream, "Participantes:", String.valueOf(getTotalParticipants(booking)), fontBold, fontNormal, yPosition);
                yPosition -= LINE_SPACING;
                drawKeyValue(contentStream, "Reservado por:", booking.getOwner().getName(), fontBold, fontNormal, yPosition);
                yPosition -= LINE_SPACING;

                String dayType = isWeekendOrHoliday(booking) ? "Fin de semana/Feriado" : "Día normal";
                drawKeyValue(contentStream, "Tipo de día:", dayType, fontBold, fontNormal, yPosition);
                yPosition -= SECTION_SPACING;

                // 4. Detalle de Pago por Integrante
                drawDetailedParticipantsTable(contentStream, booking, invoice, fontBold, fontNormal, yPosition);

                // Calculate table height and adjust yPosition
                float tableHeight = (getTotalParticipants(booking) + 1) * TABLE_ROW_HEIGHT;
                yPosition -= (tableHeight + SECTION_SPACING);

                // 5. Resumen Final
                drawFinalSummary(contentStream, invoice, booking, fontBold, fontNormal, yPosition);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private int getTotalParticipants(Booking booking) {
        Set<Client> allParticipants = new LinkedHashSet<>();
        allParticipants.add(booking.getOwner());
        allParticipants.addAll(booking.getParticipants());
        return allParticipants.size();
    }

    private boolean isWeekendOrHoliday(Booking booking) {
        int dayOfWeek = booking.getDate().getDayOfWeek().getValue();
        return dayOfWeek >= 6;
    }

    private void drawDetailedParticipantsTable(PDPageContentStream contentStream, Booking booking,
                                               Invoice invoice, PDType1Font fontHeader, PDType1Font fontContent,
                                               float yStart) throws IOException {
        drawSectionTitle(contentStream, "Detalle de Pago por Integrante", fontHeader, yStart);
        yStart -= SECTION_SPACING/2;

        float tableWidth = PDRectangle.A4.getWidth() - 2 * MARGIN;
        float[] columnWidths = {150, 80, 100, 100}; // Adjusted column widths

        // Cabecera de la tabla
        contentStream.setFont(fontHeader, 10);
        setColor(contentStream, 0.78f, 0.78f, 0.78f);
        drawTableRow(contentStream, MARGIN, yStart, tableWidth, TABLE_ROW_HEIGHT,
                new String[]{"Nombre", "Tarifa Base", "Descuentos", "Subtotal"},
                columnWidths, true);

        // Datos de los participantes
        contentStream.setFont(fontContent, 9);
        setColor(contentStream, 0, 0, 0);

        Set<Client> allParticipants = new LinkedHashSet<>();
        allParticipants.add(booking.getOwner());
        allParticipants.addAll(booking.getParticipants());

        Map<Long, ClientDiscounts> clientDiscountsMap = calculateClientDiscounts(invoice);
        double basePrice = booking.getPricing().getBasePrice();
        float currentY = yStart - TABLE_ROW_HEIGHT;

        for (Client participant : allParticipants) {
            ClientDiscounts discounts = clientDiscountsMap.getOrDefault(participant.getId(),
                    new ClientDiscounts(0, 0));

            double totalDiscount = discounts.frequentClient() + discounts.birthday();
            double subtotal = basePrice - totalDiscount;

            drawTableRow(contentStream, MARGIN, currentY, tableWidth, TABLE_ROW_HEIGHT,
                    new String[]{
                            participant.getName(),
                            formatCurrency(basePrice),
                            formatCurrency(totalDiscount, true),
                            formatCurrency(subtotal)
                    },
                    columnWidths, false);
            currentY -= TABLE_ROW_HEIGHT;
        }
    }

    private record ClientDiscounts(double frequentClient, double birthday) {}

    private Map<Long, ClientDiscounts> calculateClientDiscounts(Invoice invoice) {
        Map<Long, ClientDiscounts> result = new HashMap<>();

        invoice.getAppliedDiscounts().stream()
                .filter(d -> d.getClient() != null)
                .forEach(d -> {
                    Long clientId = d.getClient().getId();
                    ClientDiscounts current = result.getOrDefault(clientId, new ClientDiscounts(0, 0));

                    if (d.getDiscountType() == AppliedDiscount.DiscountType.FREQUENT_CLIENT) {
                        result.put(clientId, new ClientDiscounts(d.getDiscountAmount(), current.birthday()));
                    } else if (d.getDiscountType() == AppliedDiscount.DiscountType.BIRTHDAY) {
                        result.put(clientId, new ClientDiscounts(current.frequentClient(), d.getDiscountAmount()));
                    }
                });

        return result;
    }

    private void drawFinalSummary(PDPageContentStream contentStream, Invoice invoice, Booking booking,
                                  PDType1Font fontBold, PDType1Font fontNormal, float y) throws IOException {
        drawSectionTitle(contentStream, "Resumen Final", fontBold, y);
        y -= SECTION_SPACING/2;

        // Calculate subtotal from individual participant subtotals
        double subtotal = calculateSubtotal(booking, invoice);
        drawKeyValue(contentStream, "Subtotal:", formatCurrency(subtotal), fontBold, fontNormal, y);
        y -= LINE_SPACING;

        double groupDiscount = getGroupDiscount(invoice);
        drawKeyValue(contentStream, "Descuento por Grupo:",
                groupDiscount > 0 ? formatCurrency(groupDiscount, true) : "$0.00",
                fontBold, fontNormal, y);
        y -= LINE_SPACING;

        double subtotalAfterDiscount = subtotal - groupDiscount;
        drawKeyValue(contentStream, "Subtotal después de descuento:",
                formatCurrency(subtotalAfterDiscount), fontBold, fontNormal, y);
        y -= LINE_SPACING;

        double iva = subtotalAfterDiscount * taxConfiguration.getIvaPercentage();
        drawKeyValue(contentStream, "IVA (" + (taxConfiguration.getIvaPercentage() * 100) + "%):",
                formatCurrency(iva), fontBold, fontNormal, y);
        y -= LINE_SPACING;

        contentStream.setFont(fontBold, 12);
        drawKeyValue(contentStream, "TOTAL A PAGAR:",
                formatCurrency(subtotalAfterDiscount + iva), fontBold, fontBold, y);
    }

    private double calculateSubtotal(Booking booking, Invoice invoice) {
        double basePrice = booking.getPricing().getBasePrice();
        Set<Client> allParticipants = new LinkedHashSet<>();
        allParticipants.add(booking.getOwner());
        allParticipants.addAll(booking.getParticipants());

        Map<Long, ClientDiscounts> clientDiscountsMap = calculateClientDiscounts(invoice);

        return allParticipants.stream()
                .mapToDouble(participant -> {
                    ClientDiscounts discounts = clientDiscountsMap.getOrDefault(
                            participant.getId(), new ClientDiscounts(0, 0));
                    return basePrice - (discounts.frequentClient() + discounts.birthday());
                })
                .sum();
    }

    private double getGroupDiscount(Invoice invoice) {
        return invoice.getAppliedDiscounts().stream()
                .filter(d -> d.getDiscountType() == AppliedDiscount.DiscountType.GROUP)
                .mapToDouble(AppliedDiscount::getDiscountAmount)
                .sum();
    }

    private void drawSectionTitle(PDPageContentStream contentStream, String title,
                                  PDType1Font font, float y) throws IOException {
        contentStream.setFont(font, 14);
        setColor(contentStream, 0, 0, 0.4f);
        drawText(contentStream, title, MARGIN, y);
        setColor(contentStream, 0, 0, 0);
    }

    private void drawKeyValue(PDPageContentStream contentStream, String key, String value,
                              PDType1Font fontKey, PDType1Font fontValue, float y) throws IOException {
        contentStream.setFont(fontKey, 10);
        drawText(contentStream, key, MARGIN, y);
        contentStream.setFont(fontValue, 10);
        drawText(contentStream, value, MARGIN + 150, y);
    }

    private void drawTableRow(PDPageContentStream contentStream, float x, float y,
                              float tableWidth, float rowHeight, String[] texts,
                              float[] columnWidths, boolean isHeader) throws IOException {
        if (!isHeader) {
            setColor(contentStream, 0.96f, 0.96f, 0.96f);
            contentStream.addRect(x, y - rowHeight, tableWidth, rowHeight);
            contentStream.fill();
            setColor(contentStream, 0, 0, 0);
        }

        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + tableWidth, y);
        contentStream.moveTo(x, y - rowHeight);
        contentStream.lineTo(x + tableWidth, y - rowHeight);
        contentStream.stroke();

        float currentX = x;
        for (int i = 0; i < texts.length; i++) {
            float cellWidth = columnWidths[i];
            drawText(contentStream, texts[i], currentX + TABLE_CELL_MARGIN, y - 15);
            currentX += cellWidth;

            if (i < texts.length - 1) {
                contentStream.moveTo(currentX, y);
                contentStream.lineTo(currentX, y - rowHeight);
                contentStream.stroke();
            }
        }
    }

    private void drawCenteredText(PDPageContentStream contentStream, String text,
                                  PDType1Font font, float fontSize, float y) throws IOException {
        contentStream.setFont(font, fontSize);
        float titleWidth = font.getStringWidth(text) / 1000 * fontSize;
        float x = (PDRectangle.A4.getWidth() - titleWidth) / 2;
        drawText(contentStream, text, x, y);
    }

    private void drawText(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    private void setColor(PDPageContentStream contentStream, float r, float g, float b) throws IOException {
        contentStream.setNonStrokingColor(r, g, b);
    }

    private String formatCurrency(double amount) {
        return String.format("$%,.2f", amount);
    }

    private String formatCurrency(double amount, boolean isDiscount) {
        if (isDiscount && amount > 0) {
            return String.format("$-%,.2f", amount);
        }
        return formatCurrency(amount);
    }
}