package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Ticket;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;

@ApplicationScoped
public class PdfService {

    public String generateTicketPdf(Ticket ticket) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Ticket Number: " + ticket.getTicketNumber());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Event: " + ticket.getTitle());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Date: " + ticket.getEventDate());
            contentStream.newLineAtOffset(0, -20);
            contentStream.endText();
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
}