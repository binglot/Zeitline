package org.Zeitline.GUI.Action;

import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.EventTree;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
* Created by IntelliJ IDEA.
* User: Bart
* Date: 15/02/12
* Time: 10:10
* To change this template use File | Settings | File Templates.
*/
public class DeleteTimelineAction extends AbstractAction {

    private Zeitline zeitline;

    public DeleteTimelineAction(Zeitline zeitline, String text, ImageIcon icon, int mnemonic) {
        super(text, icon);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK));
        putValue(SHORT_DESCRIPTION, "Delete timeline");
    } // DeleteTimelineAction

    public void actionPerformed(ActionEvent e) {
        EventTree currentTree = zeitline.getTimelines().getCurrentTree();

        if ((((ComplexEvent) currentTree.getModel().getRoot()).countChildren() != 0)
                || zeitline.getTimelines().isOrphan(currentTree))
            return;

        zeitline.getTimelines().deleteTree(currentTree);
        zeitline.getSaveAction().setEnabled(true);
    } // actionPerformed

} // class DeleteTimelineAction
