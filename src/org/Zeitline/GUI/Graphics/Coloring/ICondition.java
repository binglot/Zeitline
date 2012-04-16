package org.Zeitline.GUI.Graphics.Coloring;

import javax.swing.tree.DefaultTreeCellRenderer;

public interface ICondition {
    boolean match(FormatDataEntry entry);
    void format(DefaultTreeCellRenderer renderer);
}
