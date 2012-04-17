package org.Zeitline.GUI.Action;

import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.NewQueryDlg;
import org.Zeitline.Query;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FilterQueryAction extends AbstractAction {

    private static final String NAME = "Filter ...";
    private static final String DESCRIPTION = "Filter event view";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK);

    private Zeitline zeitline;

    public FilterQueryAction(Zeitline zeitline, int mnemonic) {
        super(NAME);
        this.zeitline = zeitline;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);

        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        EventTree currentTree = zeitline.getTimelines().getCurrentTree();

        Query q = NewQueryDlg.showDialog(zeitline.getFrame(), currentTree.getDisplay(),
                currentTree.getStartTime(),
                currentTree.getMaxStartTime(),
                null);

        if (q == null)
            return;

        zeitline.getTimelines().getCurrentTree().getDisplay().addQuery(q);
    }

}
