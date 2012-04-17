package org.Zeitline.GUI.Action;

import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class DeleteTimelineAction extends AbstractAction {

    private static final String NAME = "Delete";
    private static final String DESCRIPTION = "Delete timeline";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK);

    private Zeitline zeitline;

    public DeleteTimelineAction(Zeitline zeitline, int mnemonic) {
        super(NAME);
        this.zeitline = zeitline;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);

        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        EventTree currentTree = zeitline.getTimelines().getCurrentTree();

        if ((((ComplexEvent) currentTree.getModel().getRoot()).countChildren() != 0)
                || zeitline.getTimelines().isOrphan(currentTree))
            return;

        zeitline.getTimelines().deleteTree(currentTree);
        zeitline.getSaveAction().setEnabled(true);
    }

}