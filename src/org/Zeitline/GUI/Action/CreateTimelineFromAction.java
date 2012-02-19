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
public class CreateTimelineFromAction extends AbstractAction {

    private Zeitline zeitline;

    public CreateTimelineFromAction(Zeitline zeitline, String text, ImageIcon icon, int mnemonic) {
        super(text, icon);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        putValue(SHORT_DESCRIPTION, "Create new timeline from selection");
    } // CreateTimelineFromAction

    public void actionPerformed(ActionEvent e) {
        ComplexEvent event = NewComplexEventDlg.showDialog(Zeitline.frame, zeitline.getTimelines(), "Create new timeline");

        if (event == null) return;

        zeitline.getTimelines().getCurrentTree().moveSelected(event, null);
        EventTree t = new EventTree(event);
        zeitline.getTimelines().addTree(t, zeitline);
        zeitline.getSaveAction().setEnabled(true);

    } // actionPerformed

} // class CreateTimelineFromAction
