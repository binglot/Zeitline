package org.Zeitline.GUI.Action;

import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.NewComplexEventDlg;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CreateTimelineFromAction extends AbstractAction {

    private static final String NAME = "Create from ...";
    private static final String DESCRIPTION = "Create new timeline from selection";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK);

    private Zeitline zeitline;

    public CreateTimelineFromAction(Zeitline zeitline, ImageIcon icon, int mnemonic) {
        super(NAME, icon);
        this.zeitline = zeitline;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);

        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        ComplexEvent event = NewComplexEventDlg.showDialog(zeitline.getFrame(), zeitline.getTimelines(), "Create new timeline");

        if (event == null)
            return;

        zeitline.getTimelines().getCurrentTree().moveSelected(event, null);

        EventTree t = new EventTree(event);
        zeitline.getTimelines().addTree(t, zeitline);
        zeitline.getSaveAction().setEnabled(true);

    }

}
