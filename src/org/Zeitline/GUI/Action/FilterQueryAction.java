package org.Zeitline.GUI.Action;

import org.Zeitline.EventTree;
import org.Zeitline.NewQueryDlg;
import org.Zeitline.Query;
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
public class FilterQueryAction extends AbstractAction {

    private Zeitline zeitline;

    public FilterQueryAction(Zeitline zeitline, String text, ImageIcon icon, int mnemonic) {
        super(text, icon);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        putValue(SHORT_DESCRIPTION, "Filter event view");
    } // FilterQueryAction

    public void actionPerformed(ActionEvent e) {
        EventTree currentTree = zeitline.getTimelines().getCurrentTree();

        Query q = NewQueryDlg.showDialog(Zeitline.frame, currentTree.getDisplay(),
                currentTree.getStartTime(),
                currentTree.getMaxStartTime(),
                null);

        if (q == null)
            return;

        zeitline.getTimelines().getCurrentTree().getDisplay().addQuery(q);
    } // actionPerformed

} // class FilterQueryAction
