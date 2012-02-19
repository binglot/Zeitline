package org.Zeitline.GUI.Action;

import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.EventTree;
import org.Zeitline.NewComplexEventDlg;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
* Created by IntelliJ IDEA.
* User: Bart
* Date: 14/02/12
* Time: 19:27
* To change this template use File | Settings | File Templates.
*/
public class EmptyTimelineAction extends AbstractAction {

    private Zeitline zeitline;

    public EmptyTimelineAction(Zeitline zeitline, String text, ImageIcon icon, int mnemonic) {
        super(text, icon);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        putValue(SHORT_DESCRIPTION, "Create empty timeline");
    } // EmptyTimelineAction

    public void actionPerformed(ActionEvent e) {
        ComplexEvent event = NewComplexEventDlg.showDialog(zeitline.getFrame(), zeitline.getTimelines(), "Create empty timeline");

        if (event == null) return;

        EventTree t = new EventTree(event);
        zeitline.getTimelines().addTree(t, zeitline);
        zeitline.getSaveAction().setEnabled(true);
    } // actionPerformed

} // class EmptyTimelineAction
