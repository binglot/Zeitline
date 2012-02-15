package org.Zeitline.GUI.Action;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
* Created by IntelliJ IDEA.
* User: Bart
* Date: 15/02/12
* Time: 10:13
* To change this template use File | Settings | File Templates.
*/ // action used to test certain functionality at the push of a button
public class TestAction extends AbstractAction {

    private Zeitline zeitline;

    public TestAction(Zeitline zeitline, String text, int mnemonic) {
        super(text);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
    }

    public void actionPerformed(ActionEvent e) {

//	    System.out.println(HostDlg.showDialog(frame));

    }
}
