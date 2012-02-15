package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
* Created by IntelliJ IDEA.
* User: Bart
* Date: 15/02/12
* Time: 10:14
* To change this template use File | Settings | File Templates.
*/
public class RemoveEventsAction extends AbstractAction {

    private Zeitline zeitline;

    public RemoveEventsAction(Zeitline zeitline, String text, ImageIcon icon, int mnemonic) {
        super(text, icon);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        putValue(SHORT_DESCRIPTION, "Delete selected events");
    }

    public void actionPerformed(ActionEvent e) {
        zeitline.timelines.removeSelected(zeitline.timelines.getCurrentTree());
        zeitline.saveAction.setEnabled(true);
    }
}
