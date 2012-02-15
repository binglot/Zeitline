package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
* Created by IntelliJ IDEA.
* User: Bart
* Date: 15/02/12
* Time: 10:12
* To change this template use File | Settings | File Templates.
*/
public class ToggleOrphanAction extends AbstractAction {

    private Zeitline zeitline;

    public ToggleOrphanAction(Zeitline zeitline, String text, ImageIcon icon, int mnemonic) {
        super(text, icon);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
    } // ToggleOrphanAction

    public void actionPerformed(ActionEvent e) {
        zeitline.timelines.toggleOrphanVisible();
    } // actionPerformed

} // class ToggleOrphanAction
