package org.Zeitline.GUI.Action;

import com.itextpdf.text.DocumentException;
import org.Zeitline.Plugin.Output.PdfCreator;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class ExportAction extends AbstractAction {
    private final PdfCreator pdfCreator;

    public ExportAction(Zeitline app) {
        pdfCreator = new PdfCreator(app);

        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // This fixed string will obviously need replacing for a dialog box
        try {
            pdfCreator.print("C:\\test\\test.pdf");
        } catch (IOException | DocumentException e1) {
            e1.printStackTrace();
        }
    }
}
