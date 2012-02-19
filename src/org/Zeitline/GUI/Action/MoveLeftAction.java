package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
* Created by IntelliJ IDEA.
* User: Bart
* Date: 15/02/12
* Time: 10:11
* To change this template use File | Settings | File Templates.
*/
public class MoveLeftAction extends AbstractAction {

    private Zeitline zeitline;

    public MoveLeftAction(Zeitline zeitline, String text, ImageIcon icon, int mnemonic) {
        super(text, icon);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.CTRL_MASK));
        putValue(SHORT_DESCRIPTION, "Move current timeline to left");
    } // MoveLeftAction

    public void actionPerformed(ActionEvent e) {
        zeitline.getTimelines().moveLeft();
        zeitline.getSaveAction().setEnabled(true);
    } // actionPerformed

} // class MoveLeftAction
