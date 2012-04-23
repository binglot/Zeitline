package org.Zeitline.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public interface IFormGenerator {
    JPanel createForm(JLabel[] items);
    IFormItem getFormItem(String label, Component component);
}
