package org.Zeitline.GUI;

import java.awt.*;

class FormItem implements IFormItem {

    private String labelText;
    private Component component;

    FormItem(String l, Component c) {
        labelText = l;
        component = c;
    }

    @Override
    public String getLabelText() {
        return labelText;
    }

    @Override
    public Component getComponent() {
        return component;
    }

}
