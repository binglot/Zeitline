package org.Zeitline.GUI.Action;

import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.NewComplexEventDlg;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class EmptyTimelineAction extends AbstractAction {

    private static final String NAME = "Create empty ...";
    private static final String DESCRIPTION = "Create empty timeline";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK);

    private Zeitline zeitline;

    public EmptyTimelineAction(Zeitline zeitline, int mnemonic) {
        super(NAME);
        this.zeitline = zeitline;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
    }

    public void actionPerformed(ActionEvent e) {
        ComplexEvent event = NewComplexEventDlg.showDialog(zeitline.getFrame(), zeitline.getTimelines(), DESCRIPTION);

        if (event == null)
            return;

        EventTree t = new EventTree(event);
        zeitline.getTimelines().addTree(t, zeitline);
        zeitline.getSaveAction().setEnabled(true);
    }

}
