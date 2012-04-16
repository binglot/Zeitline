package org.Zeitline.GUI.Graphics.Coloring.Conditions;

import org.Zeitline.GUI.Graphics.Coloring.FormatDataEntry;
import org.Zeitline.GUI.Graphics.Coloring.ICondition;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class Execution implements ICondition {
    @Override
    public boolean match(FormatDataEntry entry) {

        //
        // 16/04/2012: I've commented the rest of checks because they are redundant.
        //             Please correct me if I'm wrong.
        //

        // The text 'typed the following cmd' is what the field Description starts with (startsWith()).
//        if (entry.getDesc().startsWith("typed the following cmd"))
//            return true;

        // The text 'CMD typed' is the value of the Type entry (equals()).
//        if (entry.getShortDesc().contains("CMD typed"))
//            return true;

        // In Rob's template it's "RunMRU key" and that's the value of the Source Type entry (equals()).
        if (entry.getShortDesc().startsWith("RunMRU value"))
            return true;

        return false;
    }

    @Override
    public void format(DefaultTreeCellRenderer renderer) {
        renderer.setForeground(Color.RED);
    }
}
