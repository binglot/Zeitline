package org.Zeitline.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public interface IFormGenerator<T> {
    JPanel createForm(Vector items);
    T getFormItem(String label, Component component);
}
