package org.Zeitline.GUI.Graphics.Coloring.Conditions;

import org.Zeitline.GUI.Graphics.Coloring.FormatDataEntry;
import org.Zeitline.GUI.Graphics.Coloring.ICondition;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class WebHistory implements ICondition {

    @Override
    public boolean match(FormatDataEntry entry) {
        if (entry.getSource().equals("WEBHIST"))
            return true;

        if (entry.getSource().equals("LSO")) // Flash Cookie
            return true;

        return false;
    }

    @Override
    public void format(DefaultTreeCellRenderer renderer) {
        renderer.setForeground(Color.ORANGE);
    }
}
