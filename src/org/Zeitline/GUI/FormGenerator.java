package org.Zeitline.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

public final class FormGenerator implements IFormGenerator {

    public FormGenerator() {}

    @Override
    public JPanel createForm(Vector items) {

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        int ypos = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);


        for (Enumeration itemList = items.elements(); itemList.hasMoreElements(); ) {
            c.gridx = 0;
            c.gridy = ypos;
            c.anchor = GridBagConstraints.PAGE_START;
            FormItem formItem = (FormItem) (itemList.nextElement());
            JLabel label = new JLabel(formItem.getLabelText(), JLabel.TRAILING);
            contentPanel.add(label, c);

            // TODO: consider c.weight
            c.gridx = 1;
            c.anchor = GridBagConstraints.CENTER;
            Component comp = formItem.getComponent();
            label.setLabelFor(comp);
            // TODO consider flag for scrollbar
            contentPanel.add(comp, c);
            ypos++;
        }

        return contentPanel;
    }

    @Override
    public FormItem getFormItem(String label, Component component) {
        return new FormItem(label, component);
    }

}
