package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class MoveLeftAction extends AbstractAction {

    private static final String NAME = "Move Left";
    private static final String DESCRIPTION = "Move current timeline to left";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.CTRL_MASK);

    private Zeitline zeitline;

    public MoveLeftAction(Zeitline zeitline, int mnemonic) {
        super(NAME);
        this.zeitline = zeitline;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
    }

    public void actionPerformed(ActionEvent e) {
        zeitline.getTimelines().moveLeft();
        zeitline.getSaveAction().setEnabled(true);
    }

}
