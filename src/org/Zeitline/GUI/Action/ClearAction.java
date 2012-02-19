package org.Zeitline.GUI.Action;

import org.Zeitline.EventTree;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
* Created by IntelliJ IDEA.
* User: Bart
* Date: 14/02/12
* Time: 19:30
* To change this template use File | Settings | File Templates.
*/
public class ClearAction extends AbstractAction {

    private Zeitline zeitline;

    public ClearAction(Zeitline zeitline, String text, int mnemonic) {
        super(text);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
    } // ClearAction

    public void actionPerformed(ActionEvent e) {
        EventTree current = zeitline.getTimelines().getCurrentTree();
        if (current != null)
            current.clearSelection();
    } // actionPerformed

} // class ClearAction
