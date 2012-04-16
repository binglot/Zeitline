package org.Zeitline.GUI.Graphics.Coloring;

import org.Zeitline.GUI.Graphics.Coloring.Conditions.*;

public class ConditionsRepository {

    private ICondition[] list;

    public ConditionsRepository() {
        //
        // Unfortunately, the order of the conditions matters.
        //
        list = new ICondition[] {
                new DeviceOrUsbUsage(),
                new DeletedData(),
                new FileOpening(),
                new Execution(),
                new LogFile(),

                new WebHistory(),
        };
    }

    public ICondition[] getConditions() {
        return list;
    }
}
