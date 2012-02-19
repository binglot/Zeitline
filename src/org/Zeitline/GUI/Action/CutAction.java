package org.Zeitline.GUI.Action;

import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.EventTree;
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

    private Zeitline zeitline;

    public CutAction(Zeitline zeitline, ImageIcon icon, int mnemonic) {
        super(NAME, icon);
        this.zeitline = zeitline;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
    }

    public void actionPerformed(ActionEvent e) {

        if (zeitline.getCem().isVisible() && zeitline.getCem().isModified() && (zeitline.getCem().checkUpdate() == JOptionPane.CANCEL_OPTION))
            return;

        Transferable t = ((TimeEventTransferHandler) zeitline.getTimelines().getCurrentTree().getTransferHandler()).performCut();
        if (t == null)
            return;

        if (zeitline.getCutBuffer() != null) {
            EventTree orphan = zeitline.getTimelines().getOrphanTree();
            TimeEventTransferHandler transfer = (TimeEventTransferHandler) orphan.getTransferHandler();
            ComplexEvent orphan_root = (ComplexEvent) orphan.getModel().getRoot();
            transfer.performPaste(t, orphan_root);
        }

        zeitline.setCutBuffer(t);
        zeitline.getSaveAction().setEnabled(true);
    }

}
