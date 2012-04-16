package org.Zeitline.GUI.Graphics.Coloring.Conditions;

import org.Zeitline.GUI.Graphics.Coloring.FormatDataEntry;
import org.Zeitline.GUI.Graphics.Coloring.ICondition;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class DeviceOrUsbUsage implements ICondition{
    @Override
    public boolean match(FormatDataEntry entry) {
        if (entry.getSourceType().equals("SetupAPI Log"))
            return true;

        if (entry.getSourceType().equals("MountPoints2 key"))
            return true;

        return false;
    }

    @Override
    public void format(DefaultTreeCellRenderer renderer) {
        renderer.setForeground(Color.BLUE);
    }
}
