package org.Zeitline.GUI.Action;

import org.Zeitline.Event.AbstractTimeEvent;
import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.TimelineView;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;

/* 'Load File' action */

public class LoadAction extends AbstractAction {
    private static final String NAME = "Load";
    private static final String DESCRIPTION = "Load project";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK);

    private ComplexEvent complexEvent;
    private final TimelineView timelines;
    private final Action saveAction;
    private final JFileChooser fileChooser;


    public LoadAction(ImageIcon icon, int mnemonic, TimelineView timelines, Action saveAction, JFileChooser fileChooser) {
        super(NAME, icon);
        this.fileChooser = fileChooser;
        this.timelines = timelines;
        this.saveAction = saveAction;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
    }

    public void setComplexEvent(ComplexEvent complexEvent) {
        this.complexEvent = complexEvent;
    }

    public void actionPerformed(ActionEvent e) {
        if (saveAction.isEnabled()) {
            if (promptToSaveCurrentProject(e) == JOptionPane.CANCEL_OPTION)
                return;
        }

        ObjectInputStream inputStream = null;
        complexEvent = null;

        if (fileChooser.showOpenDialog(Zeitline.frame) != JFileChooser.APPROVE_OPTION)
            return;

        File chosen = fileChooser.getSelectedFile();
        try {
            inputStream = new ObjectInputStream(new FileInputStream(chosen));
            long eventId = (Long) inputStream.readObject();

            AbstractTimeEvent.setIdCounter(eventId);
            timelines.loadFromFile(inputStream);
            inputStream.close();
        } catch (IOException io) {
            if (io instanceof StreamCorruptedException) {
                // TODO: this is not really a check whether the file is in the proper format.
                // All we do right now is to make sure a proper Java Stream is opened.
                JOptionPane.showMessageDialog(null,
                        "The file you specified is not in the proper Zeitline project format.\n If you want to add events, choose the 'Import' function.",
                        "Invalid format",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "The following error occurred when trying to access file '" + chosen + "': " + io,
                        "I/O error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException cnf) {
            System.err.println("ERROR: ClassNotFoundException while writing ID counter");
        }

        saveAction.setEnabled(false);
    }

    private int promptToSaveCurrentProject(ActionEvent event) {
        final String message = "Would you like to save the current project before loading a different one?";

        int confirmation = JOptionPane.showConfirmDialog(null, message);
        switch (confirmation) {
            case JOptionPane.CANCEL_OPTION:
                return JOptionPane.CANCEL_OPTION;
            case JOptionPane.NO_OPTION:
                break;
            case JOptionPane.YES_OPTION:
                saveAction.actionPerformed(event);
                break;
            default:
                System.err.println("JOptionPane.showConfirmDialog() returned the unknown value of "
                        + confirmation);
        }

        return 0;
    }

}
