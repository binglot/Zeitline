package org.Zeitline.GUI.Action;

import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.EventTree;
import org.Zeitline.EventTreeModel;
import org.Zeitline.NewComplexEventDlg;
import org.Zeitline.Zeitline;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
* Created by IntelliJ IDEA.
* User: Bart
* Date: 14/02/12
* Time: 19:32
* To change this template use File | Settings | File Templates.
*/
public class CreateFromAction extends AbstractAction {

    private Zeitline zeitline;

    public CreateFromAction(Zeitline zeitline, String text, ImageIcon icon, int mnemonic) {
        super(text, icon);
        this.zeitline = zeitline;
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        putValue(SHORT_DESCRIPTION, "Create new event from selection");
    } // CreateFromAction

    public void actionPerformed(ActionEvent e) {
        EventTree currentTree = zeitline.getTimelines().getCurrentTree();

        ComplexEvent event = NewComplexEventDlg.showDialog(Zeitline.frame, currentTree.getDisplay(), "Create new event");

        if (event == null) return;

        ComplexEvent target = currentTree.getTopSelectionParent();

        boolean saveDeleteValue = target.getDeleteEmptyEvent();
        target.setDeleteEmptyEvent(false);

        currentTree.moveSelected(event, null);
        EventTreeModel model = (EventTreeModel) currentTree.getModel();

        model.insertNode(target, event);

        target.setDeleteEmptyEvent(saveDeleteValue);

        TreePath path = model.getTreePath(event);
        currentTree.expandPath(path);
        currentTree.centerEvent(event);

        zeitline.getSaveAction().setEnabled(true);

    } // actionPerformed

} // class CreateFromAction
