package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CloseAction extends AbstractAction {
    private static final String NAME = "Close";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK);
    private final Zeitline app;


    public CloseAction(Zeitline app, int mnemonic) {
        super(NAME);
        this.app = app;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);

        this.setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        app.getTimelines().clearSelections();
        app.getTimelines().closeAll();

        this.setEnabled(false);
        app.getSaveAction().setEnabled(false);
        //System.exit(0);
    }

}
