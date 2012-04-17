package org.Zeitline.GUI.Action;

import org.Zeitline.Event.ComplexEvent;
import org.Zeitline.GUI.EventTree.EventTree;
import org.Zeitline.GUI.EventTree.EventTreeModel;
import org.Zeitline.NewComplexEventDlg;
import org.Zeitline.Zeitline;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CreateFromAction extends AbstractAction {
    private static final String NAME = "Create from ...";
    private static final String DESCRIPTION = "Create new event from selection";
    private final static KeyStroke KEY_SHORTCUT = KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK);

    private Zeitline app;

    public CreateFromAction(Zeitline app, int mnemonic) {
        super(NAME);
        this.app = app;

        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KEY_SHORTCUT);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);

        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        EventTree currentTree = app.getTimelines().getCurrentTree();
        ComplexEvent event = NewComplexEventDlg.showDialog(app.getFrame(), currentTree.getDisplay(), "Create new event");

        if (event == null)
            return;

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

        app.getSaveAction().setEnabled(true);
    }

}