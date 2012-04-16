package org.Zeitline.GUI.Graphics.Coloring.Conditions;

import org.Zeitline.GUI.Graphics.Coloring.FormatDataEntry;
import org.Zeitline.GUI.Graphics.Coloring.ICondition;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class FileOpening implements ICondition {

    @Override
    public boolean match(FormatDataEntry entry) {
        if (entry.getDesc().startsWith("URL:file:///"))
            return true;

        if (entry.getSource().equals("LNK"))
            return true;

        if (entry.getShortDesc().contains("opened by"))
            return true;

        if (entry.getSourceType().equals("RecentDocs key"))
            return true;

        return false;
    }

    @Override
    public void format(DefaultTreeCellRenderer renderer) {
        renderer.setForeground(Color.CYAN);
    }
}
