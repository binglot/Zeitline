package org.Zeitline.GUI.Graphics.Coloring.Conditions;

import org.Zeitline.GUI.Graphics.Coloring.FormatDataEntry;
import org.Zeitline.GUI.Graphics.Coloring.ICondition;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class FolderOpening implements ICondition {
    @Override
    public boolean match(FormatDataEntry entry) {
        //
        // I'm not too sure about these two conditions... will have to trust Rob Lee on this one.
        //
        if (entry.getShortDesc().contains("ShellNoRoam/Bags"))
            return true;

        if (entry.getShortDesc().contains("BagMRU"))
            return true;

        return false;
    }

    @Override
    public void format(DefaultTreeCellRenderer renderer) {
        renderer.setForeground(Color.GREEN);
    }
}
