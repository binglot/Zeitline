package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class RemoveEventsAction extends AbstractAction {
    private static final String NAME = "Cut";
    private static final String DESCRIPTION = "Delete selected events";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);

    private Zeitline app;

    public RemoveEventsAction(Zeitline app, ImageIcon icon, int mnemonic) {
        super(NAME, icon);
        this.app = app;
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);

        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        app.getTimelines().removeSelected(app.getTimelines().getCurrentTree());
        app.getSaveAction().setEnabled(true);
    }
}
