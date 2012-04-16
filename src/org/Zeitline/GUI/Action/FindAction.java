package org.Zeitline.GUI.Action;

import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.NewQueryDlg;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FindAction extends AbstractAction {
    private static final String NAME = "Find ...";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK);

    private Zeitline app;

    public FindAction(Zeitline app, ImageIcon icon, int mnemonic) {
        super(NAME, icon);
        this.app = app;
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);

        setEnabled(false);
    }


    public void actionPerformed(ActionEvent e) {
        EventTree currentTree = app.getTimelines().getCurrentTree();
        final String dialogTitle = "Find Events";

        NewQueryDlg dialog = new NewQueryDlg(app.getFrame(),
                app.getMainPane().getRightComponent(),
                dialogTitle,
                false,
                NewQueryDlg.MODE_SEARCH,
                currentTree.getStartTime(),
                currentTree.getMaxStartTime(),
                null, app.getTimelines());

        dialog.setVisible(true);
    }

}
