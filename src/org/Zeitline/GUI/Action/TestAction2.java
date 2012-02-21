package org.Zeitline.GUI.Action;

import org.Zeitline.Query;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.awt.event.ActionEvent;

// action used to test certain functionality at the push of a button
public class TestAction2 extends AbstractAction {

    private Zeitline zeitline;

    public TestAction2(Zeitline zeitline, String text, int mnemonic) {
        super(text);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
    }

    public void actionPerformed(ActionEvent e) {

        /*
       org.Zeitline.EventTree tree = timelines.getCurrentTree();

       org.Zeitline.EventTreeModel currentModel = (org.Zeitline.EventTreeModel)tree.getModel();

       org.Zeitline.Event.ComplexEvent currentRoot = (org.Zeitline.Event.ComplexEvent)currentModel.getRoot();

       org.Zeitline.Event.AbstractTimeEvent res = currentRoot.findPrev(new org.Zeitline.Query("README"));

       if (res != null) {
       tree.setSelectionPath(currentModel.getTreePath(res));
       tree.centerEvent(res);
       }
       else
       System.out.println("No match found");
       */

        zeitline.getTimelines().findNextEvent(new Query("readme"), false);
    }
}
