package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class MoveRightAction extends AbstractAction {

    private static final String NAME = "Move Right";
    private static final String DESCRIPTION = "Move current timeline to right";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.CTRL_MASK);

    private Zeitline zeitline;

    public MoveRightAction(Zeitline zeitline, ImageIcon icon, int mnemonic) {
        super(NAME, icon);
        this.zeitline = zeitline;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
    }

    public void actionPerformed(ActionEvent e) {
        zeitline.getTimelines().moveRight();
        zeitline.getSaveAction().setEnabled(true);
    }

}
