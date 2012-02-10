package org.Zeitline.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public interface IFormGenerator {
    JPanel createForm(Vector items);
    FormItem getFormItem(String label, Component component);
}
