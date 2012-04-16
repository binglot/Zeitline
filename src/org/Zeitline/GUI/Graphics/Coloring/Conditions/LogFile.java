package org.Zeitline.GUI.Graphics.Coloring.Conditions;

import org.Zeitline.GUI.Graphics.Coloring.FormatDataEntry;
import org.Zeitline.GUI.Graphics.Coloring.ICondition;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class LogFile implements ICondition {
    @Override
    public boolean match(FormatDataEntry entry) {
        if (entry.getSource().contains("LOG"))
            return true;

        return false;
    }

    @Override
    public void format(DefaultTreeCellRenderer renderer) {
        renderer.setForeground(Color.GRAY);
    }
}
