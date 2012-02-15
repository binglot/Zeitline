package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
* Created by IntelliJ IDEA.
* User: Bart
* Date: 14/02/12
* Time: 19:29
* To change this template use File | Settings | File Templates.
*/
public class ExitAction extends AbstractAction {

    private Zeitline zeitline;

    public ExitAction(Zeitline zeitline, String text, int mnemonic) {
        super(text);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
    } // ExitAction

    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    } // actionPerformed

} // class ExitAction
