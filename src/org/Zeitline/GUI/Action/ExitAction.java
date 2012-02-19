package org.Zeitline.GUI.Action;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ExitAction extends AbstractAction {
    private static final String NAME = "Exit";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK);


    public ExitAction(int mnemonic) {
        super(NAME);

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
    }

    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }

}
