package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class AboutAction extends AbstractAction {
    private final String application = Zeitline.APPLICATION_NAME + " " + Zeitline.APPLICATION_VERSION;
    private final String authors = "Florian Buchholz and Courtney Falk (2004-2006)";
    private final String newAuthor = "Bartosz Inglot (2012)";
    private final String copyright = "the authors, Purdue University 2004-2006";
    private final String newCopyright = "University of Derby (2012)";

    private final Zeitline app;

    public AboutAction(Zeitline app, String text, int mnemonic) {
        super(text);
        this.app = app;

        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
    }

    public void actionPerformed(ActionEvent e) {
        final String message = application + "\n\n" + "Written by " + authors + ",\n updated by " + newAuthor + ".\n\n" +
                "Copyright " + copyright + " and\n " + newCopyright + ".";

        JOptionPane.showMessageDialog(app.getFrame(), message, Zeitline.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);//PLAIN_MESSAGE);
    }

}
