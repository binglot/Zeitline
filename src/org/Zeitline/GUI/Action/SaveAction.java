package org.Zeitline.GUI.Action;

import org.Zeitline.Event.AbstractTimeEvent;
import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.EventTree;
import org.Zeitline.TimeEventTransferHandler;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/* 'Save File' action */

public class SaveAction extends AbstractAction {
    private static final String NAME = "Save";
    private static final String DESCRIPTION = "Saved project";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK);

    private final Zeitline app;

    public SaveAction(Zeitline app, ImageIcon icon, int mnemonic) {
        super(NAME, icon);
        this.app = app;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);

        this.enabled = false;
    }

    public void actionPerformed(ActionEvent e) {
        final String dialogTitle = "Save Project";
        ObjectOutputStream outputStream = null;

        app.getFileChooser().setDialogTitle(dialogTitle);
        app.getFileChooser().setDialogType(JFileChooser.SAVE_DIALOG);
        if (app.getFileChooser().showSaveDialog(app.getFrame()) != JFileChooser.APPROVE_OPTION)
            return;

        File selectedFile = app.getFileChooser().getSelectedFile();
        String name = selectedFile.getName();

        // If the user types in a filename without a file extension, this makes sure it's there.
        if (!name.contains("."))
            selectedFile = new File(selectedFile.getParent() + File.separator + name + ".ztl");

        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(selectedFile));

            // write out the current ID counter
            outputStream.writeObject(new Long(AbstractTimeEvent.getIdCounter()));
        } catch (IOException io) {
            JOptionPane.showMessageDialog(null,
                    "The following error occurred when trying to write to the file '" + selectedFile + "': " + io,
                    "I/O error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }


        // dump cut buffer to the orphan timeline
        if (app.getCutBuffer() != null) {
            EventTree orphan = app.getTimelines().getOrphanTree();
            TimeEventTransferHandler transfer = (TimeEventTransferHandler) orphan.getTransferHandler();
            ComplexEvent orphan_root = (ComplexEvent) orphan.getModel().getRoot();
            transfer.performPaste(app.getCutBuffer(), orphan_root);
        }

        // save all the TimelineView
        app.getTimelines().saveEventTrees(outputStream);

        try {
            outputStream.close();
        } catch (IOException io) {
            JOptionPane.showMessageDialog(null,
                    "The following error occurred when trying to close the file '" + selectedFile + "': " + io,
                    "I/O error",
                    JOptionPane.ERROR_MESSAGE);
        }

        this.setEnabled(false);
    }

}
