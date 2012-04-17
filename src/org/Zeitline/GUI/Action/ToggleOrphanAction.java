package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ToggleOrphanAction extends AbstractAction {

    private static final String NAME = "Show Orphans";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK);

    private Zeitline zeitline;

    public ToggleOrphanAction(Zeitline zeitline, int mnemonic) {
        super(NAME);
        this.zeitline = zeitline;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
    }

    public void actionPerformed(ActionEvent e) {
        zeitline.getTimelines().toggleOrphanVisible();
    }

}
