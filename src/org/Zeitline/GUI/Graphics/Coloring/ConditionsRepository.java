package org.Zeitline.GUI.Graphics.Coloring;

import org.Zeitline.GUI.Graphics.Coloring.Conditions.*;

public class ConditionsRepository {

    private ICondition[] list;

    public ConditionsRepository() {
        list = new ICondition[] {
                new WebHistory(),

        };
    }

    public ICondition[] getConditions() {
        return list;
    }
}
