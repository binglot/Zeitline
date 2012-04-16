package org.Zeitline.GUI.Graphics.Coloring.Conditions;

import org.Zeitline.GUI.Graphics.Coloring.FormatDataEntry;
import org.Zeitline.GUI.Graphics.Coloring.ICondition;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class DeletedData implements ICondition {
    @Override
    public boolean match(FormatDataEntry entry) {
        if (entry.getSourceType().equals("Deleted Registry"))
            return true;

        if (entry.getSourceType().equals("$Recycle.bin"))
            return true;

        return false;
    }

    @Override
    public void format(DefaultTreeCellRenderer renderer) {
        renderer.setForeground(new Color(128, 0, 0)); // MAROON
    }
}
