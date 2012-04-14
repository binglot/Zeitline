package org.Zeitline.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public interface IFormGenerator<T extends IFormItem> {
    JPanel createForm(List<T> items);
    T getFormItem(String label, Component component);
}
