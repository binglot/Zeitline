package org.Zeitline.GUI.Action;

import org.Zeitline.EventTree;
import org.Zeitline.NewQueryDlg;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
* Created by IntelliJ IDEA.
* User: Bart
* Date: 14/02/12
* Time: 19:31
* To change this template use File | Settings | File Templates.
*/
public class FindAction extends AbstractAction {

    private Zeitline zeitline;

    public FindAction(Zeitline zeitline, String text, ImageIcon icon, int mnemonic) {
        super(text, icon);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
    } // FindAction


    public void actionPerformed(ActionEvent e) {

        EventTree currentTree = zeitline.getTimelines().getCurrentTree();

        NewQueryDlg dialog = new NewQueryDlg(Zeitline.frame,
                zeitline.getMainPane().getRightComponent(),
                "Find Events",
                false,
                NewQueryDlg.MODE_SEARCH,
                currentTree.getStartTime(),
                currentTree.getMaxStartTime(),
                null, zeitline.getTimelines());

        dialog.setVisible(true);

    } // actionPerformed

} // class FindAction
