package org.Zeitline.GUI.Action;

import org.Zeitline.Event.AbstractTimeEvent;
import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * ***********************************************************
 */

/* 'File' menu actions */

public class LoadAction extends AbstractAction {

    private ComplexEvent complex_event;
    private Zeitline zeitline;

    public LoadAction(Zeitline zeitline, String text, ImageIcon icon, int mnemonic) {
        super(text, icon);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        putValue(SHORT_DESCRIPTION, "Load project");
    } // LoadAction

    public void setComplexEvent(ComplexEvent complex_event) {
        this.complex_event = complex_event;
    } // setComplexEvent

    public void actionPerformed(ActionEvent e) {
        if (zeitline.saveAction.isEnabled()) {
            // prompt to save current project before loading
            int save_confirm = JOptionPane.showConfirmDialog(null,
                    "Would you like to save the current project before loading a different one?");
            switch (save_confirm) {
                case JOptionPane.CANCEL_OPTION:
                    // user doesn't want to continue, end loading process
                    return;
                case JOptionPane.NO_OPTION:
                    // do not save first, therefore do nothing
                    break;
                case JOptionPane.YES_OPTION:
                    // save the project first before loading another one
                    zeitline.saveAction.actionPerformed(e);
                    break;
                default:
                    System.err.println("JOptionPane.showConfirmDialog() returned "
                            + "the unknown value of "
                            + save_confirm);
            }
        }

        ObjectInputStream in_stream = null;
        complex_event = null;

        // use JFileChooser to get a file name from which to load the ComplexEvents
        if (zeitline.fileChooser.showOpenDialog(Zeitline.frame) != JFileChooser.APPROVE_OPTION) return;
        File chosen = zeitline.fileChooser.getSelectedFile();
        try {
            in_stream = new ObjectInputStream(new FileInputStream(chosen));
            long temp_long = ((Long) in_stream.readObject()).longValue();
            AbstractTimeEvent.setIdCounter(temp_long);
            zeitline.timelines.loadFromFile(in_stream, zeitline);
            in_stream.close();
        } catch (IOException io_excep) {
            if (io_excep instanceof StreamCorruptedException) {
                // TODO: this is not really a check whether the file is in the proper format.
                // All we do right now is to make sure a proper Java Stream is openend.
                JOptionPane.showMessageDialog(null,
                        "The file you specified is not in the proper org.Zeitline.Zeitline project format.\n If you want to add events, choose the 'Import' function.",
                        "Invalid format",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "The following error occurred when trying to access file '"
                                + chosen + "': " + io_excep,
                        "I/O error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException cnf_excep) {
            System.err.println("ERROR: ClassNotFoundException while writing ID counter");
        }

        zeitline.saveAction.setEnabled(false);
    } // actionPerformed

} // class LoadAction
