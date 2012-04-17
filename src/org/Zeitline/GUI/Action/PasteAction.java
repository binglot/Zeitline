package org.Zeitline.GUI.Action;

import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.TimeEventTransferHandler;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class PasteAction extends AbstractAction {
    private static final String NAME = "Paste";
    private static final String DESCRIPTION = "Paste";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, ActionEvent.SHIFT_MASK);

    private Zeitline app;

    public PasteAction(Zeitline app, int mnemonic) {
        super(NAME);
        this.app = app;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);

        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {

        if (app.getCutBuffer() == null)
            return;

        EventTree currentTree = app.getTimelines().getCurrentTree();
        if (currentTree.getSelectionCount() != 1)
            return;

        ComplexEvent targetNode;
        try {
            targetNode = (ComplexEvent) currentTree.getSelectionPath().getLastPathComponent();
        } catch (ClassCastException ce) {
            return;
        }

        ((TimeEventTransferHandler) currentTree.getTransferHandler()).performPaste(app.getCutBuffer(), targetNode);

        app.setCutBuffer(null);

        app.getSaveAction().setEnabled(true);
        app.getPasteAction().setEnabled(false);
    }

    public boolean pastePossible() {
        return (app.getCutBuffer() != null);
    }

}
