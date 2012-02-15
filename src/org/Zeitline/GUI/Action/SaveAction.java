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

/***************************************************************/
/*
 * GUI Action definitions
 */

public class SaveAction extends AbstractAction {

    private Zeitline zeitline;

    public SaveAction(Zeitline zeitline, String text, ImageIcon icon, int mnemonic) {
        super(text, icon);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        putValue(SHORT_DESCRIPTION, "Save project");
    } // SaveAction

    public void actionPerformed(ActionEvent e) {

        ObjectOutputStream out_stream = null;

        // use a JFileChooser to get a file name to save the ComplexEvents as
        zeitline.fileChooser.setDialogTitle("Save Project");
        zeitline.fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        if (zeitline.fileChooser.showSaveDialog(Zeitline.frame) != JFileChooser.APPROVE_OPTION) return;
        File chosen = zeitline.fileChooser.getSelectedFile();
        String name = chosen.getName();

        if (!name.contains("."))
            chosen = new File(chosen.getParent() + File.separator + name + ".ztl");

        // open the ObjectOutputStream
        try {
            out_stream = new ObjectOutputStream(new FileOutputStream(chosen));

            // write out the current ID counter
            out_stream.writeObject(new Long(AbstractTimeEvent.getIdCounter()));
        } catch (IOException io_excep) {
            JOptionPane.showMessageDialog(null,
                    "The following error occurred when trying to write file '"
                            + chosen + "': " + io_excep,
                    "I/O error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // dump cut buffer to the orphan timeline
        if (zeitline.cutBuffer != null) {
            EventTree orphan = zeitline.timelines.getOrphanTree();
            TimeEventTransferHandler transfer = (TimeEventTransferHandler) orphan.getTransferHandler();
            ComplexEvent orphan_root = (ComplexEvent) orphan.getModel().getRoot();
            transfer.performPaste(zeitline.cutBuffer, orphan_root);
        }

        // save all the org.Zeitline.TimelineView
        zeitline.timelines.saveEventTrees(out_stream);

        // close the ObjectOutputStream
        try {
            out_stream.close();
        } catch (IOException io_excep) {
        }

        zeitline.saveAction.setEnabled(false);

    } // actionPerformed

} // class SaveAction
