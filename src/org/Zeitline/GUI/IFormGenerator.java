package org.Zeitline.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public interface IFormGenerator<T> {
    JPanel createForm(ArrayList<T> items);
    T getFormItem(String label, Component component);
}
