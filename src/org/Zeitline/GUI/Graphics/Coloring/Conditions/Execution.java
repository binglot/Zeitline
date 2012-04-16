package org.Zeitline.GUI.Graphics.Coloring.Conditions;

import org.Zeitline.GUI.Graphics.Coloring.FormatDataEntry;
import org.Zeitline.GUI.Graphics.Coloring.ICondition;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class Execution implements ICondition {
    @Override
    public boolean match(FormatDataEntry entry) {

        // This catches Prefetch so there's no need to check for it.
        if (entry.getSource().equals("PRE"))
            return true;

        if (entry.getSourceType().equals("UserAssist key"))
            return true;

        if (entry.getSourceType().equals("RunMRU key"))
            return true;

        return false;
    }

    @Override
    public void format(DefaultTreeCellRenderer renderer) {
        renderer.setForeground(Color.RED);
    }
}
