package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class AboutAction extends AbstractAction {
    private static final String NAME = "About";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK);

    private final Zeitline app;

    public AboutAction(Zeitline app, int mnemonic) {
        super(NAME);
        this.app = app;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
    }

    private String getMessage() {
        final String application = Zeitline.APPLICATION_NAME + " " + Zeitline.APPLICATION_VERSION;
        final String description = "An attempt to resurrect a long time discontinued project by\n  ";
        final String authors = "Florian Buchholz and Courtney Falk (2004-2006)";
        final String newAuthor = "Bartosz Inglot (2012)";
        final String copyright = "the authors, Purdue University (2004-2006)";
        final String newCopyright = "University of Derby (2012)";

        final String message = application + "\n\n" + description + authors +
                ".\n\nThe code is hosted on GitHub and currently maintained by\n  " + newAuthor +
                ".\n\n" + "Copyright " + copyright + " and\n  " + newCopyright + ".";

        return message;
    }
    
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(app.getFrame(), this.getMessage(), Zeitline.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
    }

}
