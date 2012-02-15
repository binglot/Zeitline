package org.Zeitline;

import org.Zeitline.Event.AbstractTimeEvent;
import org.Zeitline.GUI.Graphics.IIconRepository;
import org.Zeitline.GUI.Graphics.IconRepository;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class EventTreeCellRenderer extends DefaultTreeCellRenderer {

    protected static ImageIcon atomicIcon, complexIcon;
    IIconRepository<ImageIcon> icons;

    EventTreeCellRenderer() {
        super();
        icons = new IconRepository();
        atomicIcon = icons.getIcon("atomic_small");
        complexIcon = icons.getIcon("complex_small");

    } // org.Zeitline.EventTreeCellRenderer

    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean selected,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {


        super.getTreeCellRendererComponent(tree, value, selected,
                expanded, leaf, row,
                hasFocus);

        /*
          if (value instanceof org.Zeitline.Event.AtomicEvent)
              setIcon(atomicIcon);
          else
              setIcon(complexIcon);
      */

        setIcon(((AbstractTimeEvent) value).getIcon());

        return this;

    }

}
