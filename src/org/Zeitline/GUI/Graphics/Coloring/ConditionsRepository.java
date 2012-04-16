package org.Zeitline.GUI.Graphics.Coloring;

import org.Zeitline.GUI.Graphics.Coloring.Conditions.*;

public class ConditionsRepository {

    private ICondition[] list;

    public ConditionsRepository() {
        //
        // Unfortunately, the order of the conditions does matter.
        //
        list = new ICondition[] {
                new DeviceOrUsbUsage(),
                new DeletedData(),
                new Execution(),
                new FileOpening(),
                new LogFile(),
                new FolderOpening(),
                new WebHistory()
        };
    }

    public ICondition[] getConditions() {
        return list;
    }
}
