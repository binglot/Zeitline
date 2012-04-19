package org.Zeitline.Plugin.Output;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.Zeitline.Event.AbstractTimeEvent;
import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.Zeitline;

import java.io.FileOutputStream;
import java.io.IOException;

public class PdfCreator {

    private final Zeitline app;

    public PdfCreator(Zeitline app) {

        this.app = app;
    }

    public void print(String filename) throws IOException, DocumentException {
        EventTree tree = app.getTimelines().getCurrentTree();
        if (tree == null || tree.isEmpty())
            return;

        ComplexEvent parent = tree.getTopSelectionParent();
        if (parent == null)
            return;


        // Instantiation of document object
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        // Creation of PdfWriter object
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();

        // Creation of table
        Paragraph title = new Paragraph("A sample output from Zeitline:",
                FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLUE));
        title.setSpacingAfter(20);
        document.add(title);

        // Setting width rations
        PdfPTable table = new PdfPTable(3);
        float[] tableWidth = {(float) 0.2, (float) 0.12, (float) 0.68};
        table.setWidths(tableWidth);

        // Setting the header
        PdfPCell dateCell = new PdfPCell(new Phrase("Date",
                FontFactory.getFont(FontFactory.TIMES_BOLD, 10, BaseColor.BLACK)));
        dateCell.setBorderColor(BaseColor.GRAY);
        dateCell.setBorderWidth(1);
        dateCell.setPadding(5);

        PdfPCell macbCell = new PdfPCell(new Phrase("MACB",
                FontFactory.getFont(FontFactory.TIMES_BOLD, 10, BaseColor.BLACK)));
        macbCell.setBorderColor(BaseColor.GRAY);
        macbCell.setBorderWidth(1);
        macbCell.setPadding(5);

        PdfPCell shortDescCell = new PdfPCell(new Phrase("Short Description",
                FontFactory.getFont(FontFactory.TIMES_BOLD, 10, BaseColor.BLACK)));
        shortDescCell.setBorderColor(BaseColor.GRAY);
        shortDescCell.setBorderWidth(1);
        shortDescCell.setPadding(5);

        table.addCell(dateCell);
        table.addCell(macbCell);
        table.addCell(shortDescCell);

        // Setting the body
        int max = parent.countChildren();
        for (int i = 0; i < max; i++) {
            AbstractTimeEvent entry = parent.getEventByIndex(i);
            table.addCell(new Phrase(entry.getStartTime().toString(),
                    FontFactory.getFont(FontFactory.TIMES, 8, BaseColor.BLACK)));

            String name = entry.getName();
            if (name != null && name.length() > 5) {
                String macb = name.substring(0,4);
                String desc = name.substring(5);

                table.addCell(new Phrase(macb,
                        FontFactory.getFont(FontFactory.TIMES, 8, BaseColor.BLACK)));
                table.addCell(new Phrase(desc,
                        FontFactory.getFont(FontFactory.TIMES, 8, BaseColor.BLACK)));
            }
            else {
                table.addCell("");
                table.addCell("");
            }
        }
        document.add(table);

        // Closure
        document.close();
        writer.close();
    }
}
