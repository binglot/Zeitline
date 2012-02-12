package org.Zeitline.GUI;

import java.awt.*;

class FormItem {

    private String labelText;
    private Component component;

    FormItem(String l, Component c) {
        labelText = l;
        component = c;
    }

    public String getLabelText() {
        return labelText;
    }

    public Component getComponent() {
        return component;
    }

}
