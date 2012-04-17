package org.Zeitline.GUI.Action;

import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.TimeEventTransferHandler;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class CutAction extends AbstractAction {
    private static final String NAME = "Cut";
    private static final String DESCRIPTION = "Cut";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.SHIFT_MASK);

    private Zeitline app;

    public CutAction(Zeitline app, int mnemonic) {
        super(NAME);
        this.app = app;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);

        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {

        if (app.getCem().isVisible() && app.getCem().isModified() && (app.getCem().checkUpdate() == JOptionPane.CANCEL_OPTION))
            return;

        Transferable t = ((TimeEventTransferHandler) app.getTimelines().getCurrentTree().getTransferHandler()).performCut();
        if (t == null)
            return;

        if (app.getCutBuffer() != null) {
            EventTree orphan = app.getTimelines().getOrphanTree();
            TimeEventTransferHandler transfer = (TimeEventTransferHandler) orphan.getTransferHandler();
            ComplexEvent orphan_root = (ComplexEvent) orphan.getModel().getRoot();
            transfer.performPaste(t, orphan_root);
        }

        app.setCutBuffer(t);
        app.getSaveAction().setEnabled(true);
    }

}
