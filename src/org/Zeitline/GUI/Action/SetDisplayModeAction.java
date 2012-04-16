package org.Zeitline.GUI.Action;

import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
* Created by IntelliJ IDEA.
* User: Bart
* Date: 15/02/12
* Time: 10:12
* To change this template use File | Settings | File Templates.
*/
public class SetDisplayModeAction extends AbstractAction {

    protected int mode;
    private Zeitline zeitline;

    public SetDisplayModeAction(Zeitline zeitline, String text, int mnemonic, int mode) {
        super(text);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        this.mode = mode;
    } // SetDisplayModeAction

    public void actionPerformed(ActionEvent e) {
        EventTree.setDisplayMode(mode);
        zeitline.getTimelines().redraw();
    } // actionPerformed

} // class SetDisplayModeAction
