package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Ticket;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.imageio.ImageIO;

@ApplicationScoped
public class PdfService {

    public String generateTicketPdf(Ticket ticket) throws IOException, WriterException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(100, 750);
            contentStream.showText("Event Ticket");
            contentStream.endText();

            PDImageXObject pdImage = PDImageXObject.createFromFile("C:\\PW5\\pw5-backend\\src\\main\\resources\\images\\logo.png", document);
            contentStream.drawImage(pdImage, 100, 600, 100, 100);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(100, 550);
            contentStream.showText("Numero Biglietto: " + ticket.getTicketNumber());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Evento: " + ticket.getTitle());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Data: " + new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss", Locale.ITALIAN).format(ticket.getEventDate()));
            contentStream.endText();

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode("Ticket ID: " + ticket.getTicketNumber(), BarcodeFormat.QR_CODE, 100, 100);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);
            PDImageXObject qrPdImage = PDImageXObject.createFromByteArray(document, baos.toByteArray(), "qr_code.png");

            contentStream.drawImage(qrPdImage, 100, 400, 100, 100);
        }

        Path filePath = Paths.get("tickets", "ticket_" + ticket.getTicketNumber() + ".pdf");
        File directory = new File(filePath.getParent().toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        document.save(filePath.toFile());
        document.close();

        return filePath.toString().replace("\\", "/");
    }

    public static class DateFormatter {
        public static void main(String[] args) {
            Date date = new Date(); // Replace with your date object
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss", Locale.ITALIAN);
            String formattedDate = formatter.format(date);
            System.out.println(formattedDate);
        }

    }
}