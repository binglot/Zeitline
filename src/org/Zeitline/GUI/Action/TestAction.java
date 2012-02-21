package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;

// action used to test certain functionality at the push of a button
public class TestAction extends AbstractAction {

    private Zeitline zeitline;

    public TestAction(Zeitline zeitline, String text, int mnemonic) {
        super(text);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, mnemonic);
    }

    public void actionPerformed(ActionEvent e) {

//	    System.out.println(HostDlg.showDialog(frame));
    }
}
