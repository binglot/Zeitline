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

import static java.util.Arrays.asList;

//
// The class is just a proof-of-concept and has many limitations:
// e.g. it stops outputting if comes across a complex event, etc.
//

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
        java.util.List<PdfPCell> headerCells = asList(
                getHeaderCell("Date"),
                getHeaderCell("MACB"),
                getHeaderCell("Short Description"));

        for(PdfPCell cell: headerCells)
                table.addCell(cell);

        // Setting the body
        int max = parent.countChildren();
        for (int i = 0; i < max; i++) {
            AbstractTimeEvent entry = parent.getEventByIndex(i);
            table.addCell(getBodyCell(entry.getStartTime().toString()));

            String name = entry.getName();
            if (name != null && name.length() > 5) {
                String macb = name.substring(0,4);
                String desc = name.substring(5);

                table.addCell(getBodyCell(macb));
                table.addCell(getBodyCell(desc));
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

    private Phrase getBodyCell(String name) {
        return new Phrase(name,
                FontFactory.getFont(FontFactory.TIMES, 8, BaseColor.BLACK));
    }

    private PdfPCell getHeaderCell(String name) {
        PdfPCell cell = new PdfPCell(new Phrase(name,
                FontFactory.getFont(FontFactory.TIMES_BOLD, 10, BaseColor.BLACK)));
        cell.setBorderColor(BaseColor.GRAY);
        cell.setBorderWidth(1);
        cell.setPadding(5);
        return cell;
    }
}
