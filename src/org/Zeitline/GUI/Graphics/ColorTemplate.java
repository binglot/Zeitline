package org.Zeitline.GUI.Graphics;

import org.Zeitline.GUI.EventTree.IEventTreeColorTemplate;
import org.Zeitline.GUI.Graphics.Coloring.ConditionsRepository;
import org.Zeitline.GUI.Graphics.Coloring.FormatDataEntry;
import org.Zeitline.GUI.Graphics.Coloring.ICondition;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ColorTemplate implements IEventTreeColorTemplate {

    private static final ConditionsRepository conditionsRepository = new ConditionsRepository();

    @Override
    public void setColor(String description, DefaultTreeCellRenderer renderer) {
        FormatDataEntry entry = new FormatDataEntry(description);

        if (!entry.isValid()){
            return;
        }

        for(ICondition condition: conditionsRepository.getConditions()) {
            if (condition.match(entry)) {
                condition.format(renderer);
                return;
            }
        }
    }

}
