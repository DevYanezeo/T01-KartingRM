package kartingRM.Services;

import kartingRM.Entities.Booking;
import kartingRM.Entities.Client;
import kartingRM.Entities.Invoice;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class PDFGeneratorServices {
    private static final Logger logger = LoggerFactory.getLogger(PDFGeneratorServices.class);
    private static final float MARGIN = 50;
    private static final float TABLE_ROW_HEIGHT = 20;
    private static final float TABLE_CELL_MARGIN = 5;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final double IVA_PERCENTAGE = 0.19; // 19%

    public byte[] generateBasicInvoice(Booking booking, Invoice invoice,
                                       Map<String, Double> discountSummary) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Configuración de fuentes
                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                float yPosition = PDRectangle.A4.getHeight() - MARGIN;

                // 1. Encabezado
                drawCenteredText(contentStream, "KARTINGRM - COMPROBANTE DE RESERVA", fontBold, 18, yPosition);
                yPosition -= 30;

                // 2. Información de la reserva
                drawSectionTitle(contentStream, "Información de la Reserva", fontBold, yPosition);
                yPosition -= 25;

                drawKeyValue(contentStream, "Código Reserva:", booking.getReservationCode(), fontBold, fontNormal, yPosition);
                yPosition -= 20;
                drawKeyValue(contentStream, "Fecha:", booking.getDate().format(DATE_FORMATTER), fontBold, fontNormal, yPosition);
                yPosition -= 20;
                drawKeyValue(contentStream, "Hora:", booking.getStartTime().format(TIME_FORMATTER), fontBold, fontNormal, yPosition);
                yPosition -= 20;
                drawKeyValue(contentStream, "Vueltas/Tiempo:", getLapsAndTimeDescription(booking), fontBold, fontNormal, yPosition);
                yPosition -= 20;
                drawKeyValue(contentStream, "Reservado por:", booking.getOwner().getName(), fontBold, fontNormal, yPosition);
                yPosition -= 20;
                drawKeyValue(contentStream, "Clientes:", String.valueOf(booking.getParticipants().size()), fontBold, fontNormal, yPosition);
                yPosition -= 30;

                // 3. Detalle de participantes (tabla)
                drawParticipantsTable(contentStream, booking, fontBold, fontNormal, yPosition);
                yPosition -= (booking.getParticipants().size() + 2) * TABLE_ROW_HEIGHT + 20;

                // 4. Resumen de pago
                drawPaymentSummary(contentStream, invoice, discountSummary, fontBold, fontNormal, yPosition);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private String getLapsAndTimeDescription(Booking booking) {
        return booking.getLaps() + " vueltas o máx " +
                (booking.getLaps() == 10 ? "10 min" :
                        booking.getLaps() == 15 ? "15 min" : "20 min");
    }

    private void drawSectionTitle(PDPageContentStream contentStream, String title,
                                  PDType1Font font, float y) throws IOException {
        contentStream.setFont(font, 14);
        setColor(contentStream, 0, 0, 0.4f); // Azul oscuro (R=0, G=0, B=0.4)
        drawText(contentStream, title, MARGIN, y);
        setColor(contentStream, 0, 0, 0); // Negro
    }

    private void drawKeyValue(PDPageContentStream contentStream, String key, String value,
                              PDType1Font fontKey, PDType1Font fontValue, float y) throws IOException {
        contentStream.setFont(fontKey, 10);
        drawText(contentStream, key, MARGIN, y);
        contentStream.setFont(fontValue, 10);
        drawText(contentStream, value, MARGIN + 150, y);
    }

    private void drawParticipantsTable(PDPageContentStream contentStream, Booking booking,
                                       PDType1Font fontHeader, PDType1Font fontContent, float yStart) throws IOException {
        // Configuración de la tabla
        float tableWidth = PDRectangle.A4.getWidth() - 2 * MARGIN;
        float[] columnWidths = {200, 100, 100, 100};

        // Cabecera de la tabla
        contentStream.setFont(fontHeader, 10);
        setColor(contentStream, 0.78f, 0.78f, 0.78f); // Gris claro (R=200, G=200, B=200 en 0-1)

        // Dibujar fila de cabecera
        drawTableRow(contentStream, MARGIN, yStart, tableWidth, TABLE_ROW_HEIGHT,
                new String[]{"Cliente", "Tarifa Base", "Descuentos", "Total"},
                columnWidths, true);

        // Datos de los participantes
        contentStream.setFont(fontContent, 9);
        setColor(contentStream, 0, 0, 0); // Negro

        // Luego los demás participantes
        float currentY = yStart - 2 * TABLE_ROW_HEIGHT;
        for (Client participant : booking.getParticipants()) {
            drawParticipantRow(contentStream, MARGIN, currentY, tableWidth,
                    participant, booking.getPricing().getBasePrice(),
                    columnWidths);
            currentY -= TABLE_ROW_HEIGHT;
        }
    }

    private void drawParticipantRow(PDPageContentStream contentStream, float x, float y,
                                    float tableWidth, Client participant, double basePrice,
                                    float[] columnWidths) throws IOException {
        // Calcular descuentos
        double discounts = calculateDiscountsForParticipant(participant, basePrice);
        double total = basePrice - discounts;

        drawTableRow(contentStream, x, y, tableWidth, TABLE_ROW_HEIGHT,
                new String[]{
                        participant.getName(),
                        formatCurrency(basePrice),
                        formatCurrency(discounts),
                        formatCurrency(total)
                },
                columnWidths, false);
    }

    private double calculateDiscountsForParticipant(Client participant, double basePrice) {
        double discount = 0;

        // Descuento por cliente frecuente
        if (participant.getMonthlyVisits() >= 5) {
            discount += basePrice * 0.2; // 20% descuento
        }

        // Descuento por cumpleaños
        if (participant.isBirthdayToday()) {
            discount += basePrice * 0.5; // 50% descuento
        }

        return discount;
    }

    private void drawPaymentSummary(PDPageContentStream contentStream, Invoice invoice,
                                    Map<String, Double> discountSummary,
                                    PDType1Font fontBold, PDType1Font fontNormal,
                                    float y) throws IOException {
        drawSectionTitle(contentStream, "Resumen de Pago", fontBold, y);
        y -= 25;

        double subtotal = invoice.getTotalToPay() / (1 + IVA_PERCENTAGE);
        double iva = subtotal * IVA_PERCENTAGE;

        drawKeyValue(contentStream, "Subtotal:", formatCurrency(subtotal), fontBold, fontNormal, y);
        y -= 20;

        // Mostrar resumen de descuentos
        if (discountSummary != null && !discountSummary.isEmpty()) {
            drawSectionTitle(contentStream, "Descuentos Aplicados:", fontBold, y);
            y -= 20;

            for (Map.Entry<String, Double> entry : discountSummary.entrySet()) {
                String discountText = String.format("%s: -%s",
                        entry.getKey(),
                        formatCurrency(entry.getValue()));

                drawText(contentStream, discountText, MARGIN + 20, y);
                y -= 15;
            }
            y -= 10;
        }

        drawKeyValue(contentStream, "IVA (19%):", formatCurrency(iva), fontBold, fontNormal, y);
        y -= 20;
        contentStream.setFont(fontBold, 12);
        drawKeyValue(contentStream, "TOTAL A PAGAR:", formatCurrency(invoice.getTotalToPay()), fontBold, fontBold, y);
    }

    private String formatCurrency(double amount) {
        return String.format("$%,.0f", amount);
    }

    private void drawTableRow(PDPageContentStream contentStream, float x, float y,
                              float tableWidth, float rowHeight, String[] texts,
                              float[] columnWidths, boolean isHeader) throws IOException {
        // Dibujar fondo para filas alternas
        if (!isHeader) {
            setColor(contentStream, 0.96f, 0.96f, 0.96f); // Gris muy claro (R=245, G=245, B=245 en 0-1)
            contentStream.addRect(x, y - rowHeight, tableWidth, rowHeight);
            contentStream.fill();
            setColor(contentStream, 0, 0, 0); // Negro
        }

        // Dibujar bordes
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + tableWidth, y);
        contentStream.moveTo(x, y - rowHeight);
        contentStream.lineTo(x + tableWidth, y - rowHeight);
        contentStream.stroke();

        // Dibujar texto en celdas
        float currentX = x + TABLE_CELL_MARGIN;
        for (int i = 0; i < texts.length; i++) {
            drawText(contentStream, texts[i], currentX, y - 15);
            currentX += columnWidths[i];

            // Dibujar línea vertical entre columnas
            if (i < texts.length - 1) {
                contentStream.moveTo(currentX, y);
                contentStream.lineTo(currentX, y - rowHeight);
                contentStream.stroke();
                currentX += TABLE_CELL_MARGIN;
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

    /**
     * Método auxiliar para establecer colores de forma segura (valores entre 0 y 1)
     */
    private void setColor(PDPageContentStream contentStream, float r, float g, float b) throws IOException {
        if (r < 0 || r > 1 || g < 0 || g > 1 || b < 0 || b > 1) {
            throw new IllegalArgumentException("Los valores de color deben estar entre 0 y 1");
        }
        contentStream.setNonStrokingColor(r, g, b);
    }
}