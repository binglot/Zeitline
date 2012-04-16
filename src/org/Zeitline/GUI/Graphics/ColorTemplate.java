package org.Zeitline.GUI.Graphics;

import org.Zeitline.GUI.EventTree.IEventTreeColorTemplate;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ColorTemplate implements IEventTreeColorTemplate {

    private static final int FIELDS_NUMBER = 13;

    @Override
    public void setColor(String description, DefaultTreeCellRenderer renderer) {
        String fields[] = description.split("\n");

        if (fields == null || fields.length != FIELDS_NUMBER)
            return;

        for(String entry: fields) {
            if (entry.endsWith("WEBHIST")){
                renderer.setForeground(Color.RED);

                return;
            }
        }
    }
}
