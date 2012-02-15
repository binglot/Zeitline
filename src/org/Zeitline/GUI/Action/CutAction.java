package org.Zeitline.GUI.Action;

import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.EventTree;
import org.Zeitline.TimeEventTransferHandler;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
* Created by IntelliJ IDEA.
* User: Bart
* Date: 14/02/12
* Time: 19:30
* To change this template use File | Settings | File Templates.
*/
public class CutAction extends AbstractAction {

    private Zeitline zeitline;

    public CutAction(Zeitline zeitline, String text, ImageIcon icon, int mnemonic) {
        super(text, icon);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.SHIFT_MASK));
        putValue(SHORT_DESCRIPTION, "Cut");
    } // CutAction

    public void actionPerformed(ActionEvent e) {

        if (zeitline.cem.isVisible() && zeitline.cem.isModified() && (zeitline.cem.checkUpdate() == JOptionPane.CANCEL_OPTION))
            return;

        Transferable t = ((TimeEventTransferHandler) zeitline.timelines.getCurrentTree().getTransferHandler()).performCut();
        if (t == null)
            return;

        if (zeitline.cutBuffer != null) {
            EventTree orphan = zeitline.timelines.getOrphanTree();
            TimeEventTransferHandler transfer = (TimeEventTransferHandler) orphan.getTransferHandler();
            ComplexEvent orphan_root = (ComplexEvent) orphan.getModel().getRoot();
            transfer.performPaste(t, orphan_root);
        }

        zeitline.cutBuffer = t;
        zeitline.saveAction.setEnabled(true);

    } // actionPerformed

} // class CutAction
