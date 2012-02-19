package org.Zeitline.GUI.Action;

import org.Zeitline.EventTree;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ClearAction extends AbstractAction {
    private static final String NAME = "Clear Selection";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK);

    private Zeitline app;

    public ClearAction(Zeitline app, int mnemonic) {
        super(NAME);
        this.app = app;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);

        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        EventTree current = app.getTimelines().getCurrentTree();
        if (current != null)
            current.clearSelection();
    }

}
