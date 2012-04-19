package org.Zeitline.Plugin.Output;

import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.Zeitline.TimelineView;

import com.itextpdf.text.*;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfCreator {
    private final TimelineView timeline;

    public PdfCreator(TimelineView timeline) {
        this.timeline = timeline;
    }

    public void print(String filename) throws IOException, DocumentException {
        // Instantiation of document object
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        // Creation of PdfWriter object
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:\\Test\\ITextTest.pdf"));
        document.open();

        // Creation of paragraph object
        document.add(new Paragraph("First page of the document."));
        document.add(new Paragraph("Some more text on the first page with different color and font type.",
                FontFactory.getFont(FontFactory.COURIER, 14, Font.BOLD, BaseColor.RED)));

        // Creation of chapter object
        Paragraph title1 = new Paragraph("Chapter 1", FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLDITALIC, BaseColor.BLACK));
        Chapter chapter1 = new Chapter(title1, 1);
        chapter1.setNumberDepth(0);

        // Creation of section object
        Paragraph title11 = new Paragraph("This is Section 1 in Chapter 1", FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, BaseColor.BLACK));
        Section section1 = chapter1.addSection(title11);
        Paragraph someSectionText = new Paragraph("This text comes as part of section 1 of chapter 1.");
        section1.add(someSectionText);
        someSectionText = new Paragraph("Following is a 3 X 2 table.");
        someSectionText.setSpacingAfter(10);
        section1.add(someSectionText);

        // Creation of table object
        PdfPTable table = new PdfPTable(3);
        //table.set
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("Cell with colspan 3"));
        cell.setColspan(3);
        cell.setBorderColor(BaseColor.GRAY);
        cell.setBorderWidth(1);
        cell.setPadding(5);
        cell.setSpaceCharRatio(5);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Cell with rowspan 2"));
        cell.setRowspan(2);
        cell.setBorderColor(BaseColor.GRAY);
        cell.setBorderWidth(1);
        cell.setPadding(5);
        cell.setSpaceCharRatio(5);
        table.addCell(cell);
        table.addCell("row 1; cell 1");
        table.addCell("row 1; cell 2");
        table.addCell("row 2; cell 1");
        table.addCell("row 2; cell 2");
        section1.add(table);

        // Creation of list object
        List l = new List(true, false, 10);
        l.add(new ListItem("First item of list"));
        l.add(new ListItem("Second item of list"));
        section1.add(l);

        // Addition of a chapter to the main document
        document.add(chapter1);

        // Closure
        document.close();
        writer.close();
    }
}
